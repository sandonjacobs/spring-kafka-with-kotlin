package com.example.parking.util

import com.google.protobuf.Timestamp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant

/**
 * Converts a Protocol Buffer Timestamp to a Kotlin Instant.
 *
 * @return A Kotlin Instant representing the same point in time as the Timestamp
 */
fun Timestamp.toInstant(): Instant {
    return Instant.fromEpochSeconds(seconds, nanos)
}

/**
 * Converts a Kotlin Instant to a Protocol Buffer Timestamp.
 *
 * @return A Protocol Buffer Timestamp representing the same point in time as the Instant
 */
fun Instant.toTimestamp(): Timestamp {
    return Timestamp.newBuilder()
        .setSeconds(epochSeconds)
        .setNanos(nanosecondsOfSecond)
        .build()
}

/**
 * Creates a Protocol Buffer Timestamp representing the current system time.
 *
 * @return A Protocol Buffer Timestamp for the current system time
 */
fun currentTimestamp(): Timestamp {
    return Clock.System.now().toTimestamp()
} 