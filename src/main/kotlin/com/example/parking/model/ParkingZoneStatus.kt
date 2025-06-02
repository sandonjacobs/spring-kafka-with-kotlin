package com.example.parking.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Represents the cached status of a parking zone, including occupancy and last update time.
 *
 * This class is used by the caching layer and UI to provide real-time status for each zone.
 *
 * @property zoneId Unique identifier for the parking zone (matches ParkingZone.id)
 * @property occupiedSpots Number of currently occupied parking spots in the zone
 * @property availableSpots Number of currently available parking spots in the zone
 * @property lastUpdated Timestamp (UTC) when this status was last updated
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
         * Maps a [ZoneOccupancy] message (from Kafka Streams) to a [ParkingZoneCacheStatus] for caching and UI.
         *
         * Calculates available spots by subtracting occupied from total.
         *
         * @param occupancy The [ZoneOccupancy] message from the Kafka Streams state store
         * @return A new [ParkingZoneCacheStatus] instance with mapped data and current timestamp
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