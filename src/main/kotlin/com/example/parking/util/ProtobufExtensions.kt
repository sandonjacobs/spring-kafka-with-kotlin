package com.example.parking.util

import com.google.protobuf.Timestamp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant

fun Timestamp.toInstant(): Instant {
    return Instant.fromEpochSeconds(seconds, nanos)
}

fun Instant.toTimestamp(): Timestamp {
    return Timestamp.newBuilder()
        .setSeconds(epochSeconds)
        .setNanos(nanosecondsOfSecond)
        .build()
}

fun currentTimestamp(): Timestamp {
    return Clock.System.now().toTimestamp()
} 