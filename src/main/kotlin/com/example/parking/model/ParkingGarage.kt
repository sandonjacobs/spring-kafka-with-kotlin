package com.example.parking.model

import org.slf4j.LoggerFactory
import java.time.Instant

/**
 * Represents a parking garage with multiple zones and total capacity.
 *
 * @property id Unique identifier for the parking garage
 * @property name Display name of the parking garage
 * @property totalSpaces Total number of parking spaces across all zones
 * @property zones List of parking zones within the garage
 */
class ParkingGarage(
    val id: String,
    val name: String,
    val totalSpaces: Int,
    val zones: List<ParkingZone>
) {
    private val log = LoggerFactory.getLogger(ParkingGarage::class.java)

    /**
     * Generates a parking event for a specific zone.
     *
     * @param zoneId The ID of the zone where the event occurred
     * @param eventType The type of event (vehicle entered or exited)
     * @return A new ParkingEvent with the current timestamp
     */
    fun generateParkingEvent(zoneId: String, eventType: EventType): ParkingEvent {
        return ParkingEvent.newBuilder()
            .setZoneId(zoneId)
            .setEventType(eventType)
            .setTimestamp(Instant.now().toEpochMilli())
            .build()
    }

    /**
     * Calculates the total number of occupied spaces across all zones.
     *
     * @return The sum of occupied spaces in all zones
     */
    fun getTotalOccupiedSpaces(): Int {
        return zones.sumOf { it.occupiedSpaces }
    }
}

/**
 * Represents a zone within a parking garage.
 *
 * @property id Unique identifier for the zone
 * @property name Display name of the zone
 * @property totalSpaces Total number of parking spaces in this zone
 * @property occupiedSpaces Number of currently occupied spaces in this zone
 */
class ParkingZone(
    val id: String,
    val name: String,
    val totalSpaces: Int,
    var occupiedSpaces: Int = 0
)

/**
 * Generates a ZoneOccupancy message from this ParkingZone.
 *
 * @return A ZoneOccupancy message containing the current occupancy information
 */
fun ParkingZone.generateZoneOccupancy(): ZoneOccupancy {
    return ZoneOccupancy.newBuilder()
        .setZoneId(this.id)
        .setTotalSpaces(this.totalSpaces)
        .setOccupiedSpaces(this.occupiedSpaces)
        .build()
}
