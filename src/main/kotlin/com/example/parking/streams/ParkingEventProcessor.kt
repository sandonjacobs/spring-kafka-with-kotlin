package com.example.parking.streams

import com.example.parking.model.EventType
import com.example.parking.model.ParkingEvent
import com.example.parking.model.ZoneOccupancy
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ParkingEventProcessor(
    val parkingEventSerde: Serde<ParkingEvent>,
    val zoneOccupancySerde: Serde<ZoneOccupancy>,
    val parkingGarage: com.example.parking.model.ParkingGarage,
    @Value("\${kafka.topics.parking-events}") val parkingEventsTopic: String,
    @Value("\${kafka.topics.zone-occupancy}") val zoneOccupancyTopic: String,
    @Value("\${kafka.state-store.zone-occupancy}") val zoneOccupancyStore: String
) {
    private val log = LoggerFactory.getLogger(ParkingEventProcessor::class.java)

    @Autowired
    fun buildPipeline(builder: StreamsBuilder) {
        log.info("Building topology with topics: parking-events={}, zone-occupancy={}", parkingEventsTopic, zoneOccupancyTopic)

        // Read the compacted topic as a KTable (no explicit state store name)
        val zoneOccupancyTable = builder.table(
            zoneOccupancyTopic,
            Consumed.with(Serdes.String(), zoneOccupancySerde)
        )

        // Read the parking events as a KStream
        val parkingEventsStream = builder.stream(
            parkingEventsTopic,
            Consumed.with(Serdes.String(), parkingEventSerde)
        ).peek { _, event -> log.debug("Processing parking event: {}", event) }

        // Join the KStream to the KTable and update occupancy
        // Use a Joined configuration to ensure the join works correctly
        val joined = parkingEventsStream.leftJoin(
            zoneOccupancyTable,
            { event, occupancy ->
                log.debug("Event occupancy: {}", occupancy)
                // Get the totalSpaces value from the ParkingZone
                val prev = occupancy ?: ZoneOccupancy.newBuilder()
                    .setZoneId(event.zoneId)
                    .setTotalSpaces(0)
                    .setOccupiedSpaces(0)
                    .build()
                val outputBuilder = ZoneOccupancy.newBuilder(prev)
                when (event.eventType) {
                    EventType.VEHICLE_ENTERED -> outputBuilder.occupiedSpaces = prev.occupiedSpaces + 1
                    EventType.VEHICLE_EXITED -> outputBuilder.occupiedSpaces = (prev.occupiedSpaces - 1).coerceAtLeast(0)
                    EventType.UNRECOGNIZED, null -> log.warn("Unrecognized event type for event: {}", event)
                }
                val updated = outputBuilder.build()
                log.info("Zone {} occupancy updated: total={}, occupied={}", 
                    event.zoneId, updated.totalSpaces, updated.occupiedSpaces)
                updated
            },
            Joined.with(Serdes.String(), parkingEventSerde, zoneOccupancySerde)
        )

        // Materialize the joined stream into a state store (output store)
        val updatedOccupancyTable = joined.toTable(
            Materialized.`as`<String, ZoneOccupancy, KeyValueStore<Bytes, ByteArray>>(zoneOccupancyStore)
                .withKeySerde(Serdes.String())
                .withValueSerde(zoneOccupancySerde)
        )

        // Write the updated occupancy back to the same topic
        updatedOccupancyTable.toStream().to(zoneOccupancyTopic, Produced.with(Serdes.String(), zoneOccupancySerde))
    }
}
