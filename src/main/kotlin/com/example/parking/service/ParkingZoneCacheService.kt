package com.example.parking.service

import com.example.parking.model.ParkingZoneCacheStatus
import com.example.parking.model.ParkingZoneEvent
import com.example.parking.model.ParkingZoneEventType
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.stereotype.Service
import kotlinx.datetime.Clock
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Service responsible for caching and managing parking zone status information.
 * Uses Spring's caching abstraction with Caffeine as the underlying cache implementation.
 *
 * @property cacheManager Spring's cache manager for handling cache operations
 */
@Service
@CacheConfig(cacheNames = ["parkingZones"])
class ParkingZoneCacheService(
    private val cacheManager: CacheManager
) {

    // In-memory event log for UI animation
    private val eventLog: ConcurrentLinkedQueue<ParkingZoneEvent> = ConcurrentLinkedQueue()
    private val maxEvents = 50 // keep last 50 events

    /**
     * Retrieves the current status of a parking zone from the cache.
     * If the zone is not in cache, returns null as the cache will be populated by the Kafka consumer.
     *
     * @param zoneId The ID of the zone to retrieve status for
     * @return The current status of the zone, or null if not in cache
     */
    @Cacheable(key = "#zoneId", unless = "#result == null")
    fun getZoneStatus(zoneId: String): ParkingZoneCacheStatus? {
        // If not in cache, return null - the cache will be populated by the Kafka consumer
        return null
    }

    /**
     * Updates the status of a parking zone in the cache.
     *
     * @param status The new status to cache for the zone
     * @return The cached status
     */
    @CachePut(key = "#status.zoneId")
    fun updateZoneStatus(status: ParkingZoneCacheStatus): ParkingZoneCacheStatus {
        return status
    }

    /**
     * Retrieves the status of all parking zones currently in the cache.
     *
     * @return A list of all cached zone statuses
     * @throws IllegalStateException if the cache is not found
     */
    fun getAllZoneStatuses(): List<ParkingZoneCacheStatus> {
        val cache = cacheManager.getCache("parkingZones")
            ?: throw IllegalStateException("Cache 'parkingZones' not found")

        return (cache as CaffeineCache).nativeCache.asMap().values
            .filterIsInstance<ParkingZoneCacheStatus>()
            .toList()
    }

    fun logZoneEvent(zoneId: String, eventType: ParkingZoneEventType) {
        eventLog.add(ParkingZoneEvent(zoneId, eventType, Clock.System.now()))
        while (eventLog.size > maxEvents) eventLog.poll()
    }

    fun getRecentEvents(): List<ParkingZoneEvent> = eventLog.toList()
} 
