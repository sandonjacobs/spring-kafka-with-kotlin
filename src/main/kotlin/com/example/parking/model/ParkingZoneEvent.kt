package com.example.parking.model

import kotlinx.datetime.Instant

/**
 * Type of event occurring in a parking zone, used for UI animation and event tracking.
 *
 * - ENTER: A vehicle has entered the zone (occupancy increased)
 * - EXIT:  A vehicle has exited the zone (occupancy decreased)
 */
enum class ParkingZoneEventType {
    ENTER, EXIT
}

/**
 * Represents a recent event (entry or exit) in a parking zone, for UI animation and history.
 *
 * Used by the UI to animate car icons and show recent activity per zone.
 *
 * @property zoneId The ID of the zone where the event occurred
 * @property eventType The type of event (ENTER or EXIT)
 * @property timestamp The time the event occurred (UTC)
 */
data class ParkingZoneEvent(
    val zoneId: String,
    val eventType: ParkingZoneEventType,
    val timestamp: Instant = Instant.DISTANT_PAST // will be set when event is created
) 