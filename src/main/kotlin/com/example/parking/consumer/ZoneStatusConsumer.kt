package com.example.parking.consumer

import com.example.parking.model.ParkingZoneCacheStatus
import com.example.parking.model.ZoneOccupancy
import com.example.parking.service.ParkingZoneCacheService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

/**
 * Component responsible for consuming zone occupancy updates from Kafka and updating the cache.
 * Listens to the zone occupancy topic and maintains the current state in the cache service.
 *
 * @property cacheService Service for caching zone status information
 */
@Component
class ZoneStatusConsumer(
    private val cacheService: ParkingZoneCacheService
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    
    /**
     * Consumes zone occupancy updates from Kafka and updates the cache.
     * Converts the ZoneOccupancy message to a ParkingZoneCacheStatus and stores it in the cache.
     *
     * @param occupancy The ZoneOccupancy message received from Kafka
     */
    @KafkaListener(
        topics = ["\${kafka.topics.zone-occupancy}"],
        groupId = "zone-status-cache-updater"
    )
    fun consumeZoneStatus(occupancy: ZoneOccupancy) {
        val status = ParkingZoneCacheStatus.fromZoneOccupancy(occupancy)
        logger.info("Updating cache for zone {}: {} spots occupied, {} spots available", 
            status.zoneId, status.occupiedSpots, status.availableSpots)
        cacheService.updateZoneStatus(status)
    }
} 