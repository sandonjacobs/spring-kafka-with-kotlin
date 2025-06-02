package com.example.parking.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents the current status of a parking zone, including occupancy information and last update time.
 *
 * @property zoneId The unique identifier of the parking zone
 * @property occupiedSpots The number of currently occupied parking spots in the zone
 * @property availableSpots The number of currently available parking spots in the zone
 * @property lastUpdated The timestamp when this status was last updated
 */
@Serializable
data class ParkingZoneCacheStatus(
    val zoneId: String,
    val occupiedSpots: Int,
    val availableSpots: Int,
    val lastUpdated: Instant
) {

    companion object {
        /**
         * Creates a [ParkingZoneCacheStatus] from a [ZoneOccupancy] message.
         * 
         * This function maps the occupancy information from the Kafka Streams state store
         * to our application's cache model. It calculates the available spots by subtracting
         * the occupied spaces from the total spaces.
         *
         * @param occupancy The [ZoneOccupancy] message from the Kafka Streams state store
         * @return A new [ParkingZoneCacheStatus] instance with the mapped data and current timestamp
         */
        fun fromZoneOccupancy(occupancy: ZoneOccupancy): ParkingZoneCacheStatus {
            return ParkingZoneCacheStatus(
                zoneId = occupancy.zoneId,
                occupiedSpots = occupancy.occupiedSpaces,
                availableSpots = occupancy.totalSpaces - occupancy.occupiedSpaces,
                lastUpdated = Clock.System.now()
            )
        }
    }
} 