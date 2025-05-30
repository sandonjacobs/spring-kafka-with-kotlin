package com.example.parking.consumer

import com.example.parking.model.ParkingZoneStatus
import com.example.parking.model.ZoneOccupancy
import com.example.parking.service.ParkingZoneCacheService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ZoneStatusConsumer(
    private val cacheService: ParkingZoneCacheService
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    
    @KafkaListener(
        topics = ["\${kafka.topics.zone-occupancy}"],
        groupId = "zone-status-cache-updater"
    )
    fun consumeZoneStatus(occupancy: ZoneOccupancy) {
        val status = ParkingZoneStatus.fromZoneOccupancy(occupancy)
        logger.info("Updating cache for zone {}: {} spots occupied, {} spots available", 
            status.zoneId, status.occupiedSpots, status.availableSpots)
        cacheService.updateZoneStatus(status)
    }
} 