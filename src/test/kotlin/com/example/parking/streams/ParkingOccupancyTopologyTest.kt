package com.example.parking.streams

import com.example.parking.model.*
import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.TopologyTestDriver
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

class ParkingOccupancyTopologyTest {
    private val log = LoggerFactory.getLogger(ParkingOccupancyTopologyTest::class.java)
    private lateinit var parkingEventSerde: Serde<ParkingEvent>
    private lateinit var zoneOccupancySerde: Serde<ZoneOccupancy>

    private lateinit var testDriver: TopologyTestDriver
    private lateinit var inputParkingEvents: TestInputTopic<String, ParkingEvent>
    private lateinit var inputZoneOccupancy: TestInputTopic<String, ZoneOccupancy>
    private lateinit var outputZoneOccupancy: TestOutputTopic<String, ZoneOccupancy>

    private val parkingEventsTopic = "parking-events-proto"
    private val zoneOccupancyTopic = "parking-occupancy-by-zone"
    private val zoneOccupancyStateStore = "zone-occupancy-state-store"
    private val updatedOccupancyStateStore = "updated-occupancy-state-store"

    lateinit var processor: ParkingEventProcessor
    lateinit var parkingGarage: ParkingGarage

    @BeforeEach
    fun setup() {
        // Create a test ParkingGarage with zones that match the test cases
        parkingGarage = ParkingGarage(
            id = "test-garage",
            name = "Test Garage",
            totalSpaces = 100,
            zones = listOf(
                ParkingZone("A", "Zone A", 30),
                ParkingZone("B", "Zone B", 40),
                ParkingZone("C", "Zone C", 30)
            )
        )
        parkingEventSerde = KafkaProtobufSerde(ParkingEvent::class.java)
        parkingEventSerde.configure(
            mapOf(
                "schema.registry.url" to "mock://",
                "specific.protobuf.value.type" to ParkingEvent::class.java.name
            ), false
        )
        zoneOccupancySerde = KafkaProtobufSerde(ZoneOccupancy::class.java)
        zoneOccupancySerde.configure(
            mapOf(
                "schema.registry.url" to "mock://",
                "specific.protobuf.value.type" to ZoneOccupancy::class.java.name
            ), false
        )

        val builder = StreamsBuilder()
        processor = ParkingEventProcessor(parkingEventSerde, zoneOccupancySerde, parkingGarage,
            parkingEventsTopic, zoneOccupancyTopic, zoneOccupancyStateStore)
        processor.buildPipeline(builder)

        val topology = builder.build()
        val props = java.util.Properties()
        props.setProperty("state.dir", "build/kafka-streams-test")
        props.setProperty("application.id", "test-app")
        props.setProperty("bootstrap.servers", "dummy:1234")

        testDriver = TopologyTestDriver(topology, props)

        inputParkingEvents = testDriver.createInputTopic(
            parkingEventsTopic,
            Serdes.String().serializer(),
            parkingEventSerde.serializer()
        )
        inputZoneOccupancy = testDriver.createInputTopic(
            zoneOccupancyTopic,
            Serdes.String().serializer(),
            zoneOccupancySerde.serializer()
        )

        outputZoneOccupancy = testDriver.createOutputTopic(
            zoneOccupancyTopic,
            Serdes.String().deserializer(),
            zoneOccupancySerde.deserializer()
        )
    }

    @AfterEach
    fun tearDown() {
        testDriver.close()
    }

//    @Test
//    @Timeout(value = 5, unit = TimeUnit.SECONDS)
//    fun `should update counts when vehicle enters`() {
//        // Given
//        val zoneId = "A"
//        val event = createParkingEvent(zoneId, EventType.VEHICLE_ENTERED)
//
//        // When
//        inputParkingEvents.pipeInput(zoneId, event)
//
//        // Then
//        val output = outputZoneOccupancy.readKeyValue()
//        log.trace("Received output: key={}, value={}", output.key, output.value)
//        assertEquals(zoneId, output.key)
//        assertEquals(1, output.value.occupiedSpaces)
//        // Verify that totalSpaces is set correctly from the ParkingZone
//        assertEquals(30, output.value.totalSpaces)
//        log.debug("Test completed successfully")
//    }
//
//    @Test
//    @Timeout(value = 5, unit = TimeUnit.SECONDS)
//    fun `should update counts when vehicle exits`() {
//        // Given
//        val zoneId = "A"
//        val enterEvent = createParkingEvent(zoneId, EventType.VEHICLE_ENTERED)
//        val exitEvent = createParkingEvent(zoneId, EventType.VEHICLE_EXITED)
//
//        // When
//        inputParkingEvents.pipeInput(zoneId, enterEvent)
//        inputParkingEvents.pipeInput(zoneId, exitEvent)
//
//        // Then
//        val outputs = outputZoneOccupancy.readKeyValuesToList()
//        log.trace("Received {} outputs", outputs.size)
//        assertEquals(2, outputs.size)
//        assertEquals(0, outputs[1].value.occupiedSpaces)
//    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    fun `should update counts for existing zone when vehicle enters`() {
        // Given
        val zoneId = "A"
        val enterEvent = createParkingEvent(zoneId, EventType.VEHICLE_ENTERED)
        val existingZoneA = ZoneOccupancy.newBuilder()
            .setZoneId(zoneId)
            .setTotalSpaces(10)
            .setOccupiedSpaces(3)
            .build()

        // When
        inputZoneOccupancy.pipeInput(zoneId, existingZoneA)
        inputParkingEvents.pipeInput(zoneId, enterEvent)

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted {
            val outputs = outputZoneOccupancy.readKeyValuesToList()
            log.trace("Received {} outputs", outputs.size)
            assertEquals(4, outputs[0].value.occupiedSpaces)
        }
    }

    private fun createParkingEvent(zoneId: String, eventType: EventType): ParkingEvent {
        return ParkingEvent.newBuilder()
            .setEventId(UUID.randomUUID().toString())
            .setSpaceId("$zoneId-1")
            .setZoneId(zoneId)
            .setEventType(eventType)
            .setVehicle(
                Vehicle.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setLicensePlate("ABC-123")
                    .setState("NC")
                    .setType(VehicleType.CAR)
                    .build()
            )
            .setTimestamp(Instant.now().toEpochMilli())
            .build()
    }
}
