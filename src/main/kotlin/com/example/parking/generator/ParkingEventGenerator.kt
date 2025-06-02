package com.example.parking.generator

import com.example.parking.model.EventType
import com.example.parking.model.ParkingEvent
import com.example.parking.model.ParkingGarage
import com.example.parking.model.ParkingZone
import com.example.parking.model.ZoneOccupancy
import com.example.parking.model.generateZoneOccupancy
import com.example.parking.service.ParkingZoneCacheService
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import kotlin.random.Random

/**
 * Component responsible for generating and sending parking events to Kafka.
 * Simulates vehicle entry and exit events for different parking zones.
 *
 * @property parkingEventTemplate Kafka template for sending parking events
 * @property zoneOccupancyTemplate Kafka template for sending zone occupancy updates
 * @property parkingGarage The parking garage model containing zone information
 * @property cacheService Service for caching zone status information
 * @property parkingEventsTopic Kafka topic for parking events
 * @property zoneOccupancyTopic Kafka topic for zone occupancy updates
 */
@Component
class ParkingEventGenerator(
    private val parkingEventTemplate: KafkaTemplate<String, ParkingEvent>,
    private val zoneOccupancyTemplate: KafkaTemplate<String, ZoneOccupancy>,
    private val parkingGarage: ParkingGarage,
    private val cacheService: ParkingZoneCacheService,
    @Value("\${kafka.topics.parking-events}") val parkingEventsTopic: String,
    @Value("\${kafka.topics.zone-occupancy}") val zoneOccupancyTopic: String
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val random = Random(Instant.now().toEpochMilli())

    /**
     * Initializes the parking event generator by sending initial zone occupancy states.
     * Called after the component is constructed.
     */
    @PostConstruct
    fun init() {
        log.info("Initializing ParkingEventGenerator...")
        parkingGarage.zones.forEach { z ->
            val zo = z.generateZoneOccupancy()
            log.debug("Sending initialized ZoneOccupancy {} to topic {}", zo, zoneOccupancyTopic)
            zoneOccupancyTemplate.send(zoneOccupancyTopic, zo.zoneId, zo)
        }
    }

    /**
     * Periodically generates and sends vehicle entry events.
     * Only sends events if there are available spaces in the selected zone.
     * Runs every 1 second.
     */
    @Scheduled(fixedRate = 500)
    fun sendVehicleEnteredEvent() {
        val zone = parkingGarage.zones.random()

        val zoneAvailableSpots = cacheService.getZoneStatus(zone.id)?.availableSpots ?: -1

        if (zoneAvailableSpots > 0) {
            val event = parkingGarage.generateParkingEvent(zone.id, EventType.VEHICLE_ENTERED)
            log.debug("Sending ENTER parking event {} to topic {}", event, zone)
            parkingEventTemplate.send(parkingEventsTopic, zone.id, event)
        } else {
            log.warn("*** No available parking events for zone $zone")
        }
    }

    /**
     * Periodically generates and sends vehicle exit events.
     * Runs every 1.5 seconds with an initial delay of 5 seconds.
     */
    @Scheduled(fixedRate = 600, initialDelay = 3000)
    fun sendVehicleExitEvent() {
        val zone = parkingGarage.zones.random()
        val event = parkingGarage.generateParkingEvent(zone.id, EventType.VEHICLE_EXITED)
        log.debug("Sending EXIT parking event {} to topic {}", event, zone)
        parkingEventTemplate.send(parkingEventsTopic, zone.id, event)
    }
}