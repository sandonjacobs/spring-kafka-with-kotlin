package com.example.parking.model

import org.slf4j.LoggerFactory
import java.time.Instant

class ParkingGarage(
    val id: String,
    val name: String,
    val totalSpaces: Int,
    val zones: List<ParkingZone>
) {
    private val log = LoggerFactory.getLogger(ParkingGarage::class.java)

    fun generateParkingEvent(zoneId: String, eventType: EventType): ParkingEvent {
        return ParkingEvent.newBuilder()
            .setZoneId(zoneId)
            .setEventType(eventType)
            .setTimestamp(Instant.now().toEpochMilli())
            .build()
    }

    fun getTotalOccupiedSpaces(): Int {
        return zones.sumOf { it.occupiedSpaces }
    }
}

class ParkingZone(
    val id: String,
    val name: String,
    val totalSpaces: Int,
    var occupiedSpaces: Int = 0
)

fun ParkingZone.generateZoneOccupancy(): ZoneOccupancy {
    return ZoneOccupancy.newBuilder()
        .setZoneId(this.id)
        .setTotalSpaces(this.totalSpaces)
        .setOccupiedSpaces(this.occupiedSpaces)
        .build()
}
