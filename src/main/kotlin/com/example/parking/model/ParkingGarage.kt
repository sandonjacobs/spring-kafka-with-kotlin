package com.example.parking.model

import org.slf4j.LoggerFactory
import java.time.Instant

/**
 * Represents a parking garage containing multiple zones and overall capacity.
 *
 * @property id Unique identifier for the parking garage
 * @property name Display name of the parking garage
 * @property totalSpaces Total number of parking spaces across all zones
 * @property zones List of [ParkingZone]s within the garage
 *
 * Usage: Used as the authoritative source of garage structure for the UI and event generation.
 */
class ParkingGarage(
    val id: String,
    val name: String,
    val totalSpaces: Int,
    val zones: List<ParkingZone>
) {
    private val log = LoggerFactory.getLogger(ParkingGarage::class.java)

    /**
     * Generates a [ParkingEvent] for a specific zone and event type.
     *
     * @param zoneId The ID of the zone where the event occurred
     * @param eventType The type of event (vehicle entered or exited)
     * @return A new [ParkingEvent] with the current timestamp
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
 * Represents a single zone within a parking garage.
 *
 * @property id Unique identifier for the zone (used as zoneId in events and cache)
 * @property name Display name of the zone
 * @property totalSpaces Total number of parking spaces in this zone
 * @property occupiedSpaces Number of currently occupied spaces in this zone
 *
 * Usage: Used for garage structure, event generation, and initial UI rendering.
 */
class ParkingZone(
    val id: String,
    val name: String,
    val totalSpaces: Int,
    var occupiedSpaces: Int = 0
)

/**
 * Extension function to generate a [ZoneOccupancy] message from a [ParkingZone].
 *
 * Used for sending initial or updated occupancy information to Kafka Streams.
 *
 * @receiver The [ParkingZone] to generate occupancy for
 * @return A [ZoneOccupancy] message containing the current occupancy information
 */
fun ParkingZone.generateZoneOccupancy(): ZoneOccupancy {
    return ZoneOccupancy.newBuilder()
        .setZoneId(this.id)
        .setTotalSpaces(this.totalSpaces)
        .setOccupiedSpaces(this.occupiedSpaces)
        .build()
}
