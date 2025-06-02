package com.example.parking.model

import kotlinx.datetime.Instant

enum class ParkingZoneEventType {
    ENTER, EXIT
}

data class ParkingZoneEvent(
    val zoneId: String,
    val eventType: ParkingZoneEventType,
    val timestamp: Instant = Instant.DISTANT_PAST // will be set when event is created
) 