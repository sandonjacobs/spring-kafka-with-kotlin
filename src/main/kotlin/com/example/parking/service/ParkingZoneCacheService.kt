package com.example.parking.service

import com.example.parking.model.ParkingZoneStatus
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.stereotype.Service

@Service
@CacheConfig(cacheNames = ["parkingZones"])
class ParkingZoneCacheService(
    private val cacheManager: CacheManager
) {

    @Cacheable(key = "#zoneId", unless = "#result == null")
    fun getZoneStatus(zoneId: String): ParkingZoneStatus? {
        // If not in cache, return null - the cache will be populated by the Kafka consumer
        return null
    }

    @CachePut(key = "#status.zoneId")
    fun updateZoneStatus(status: ParkingZoneStatus): ParkingZoneStatus {
        return status
    }

    fun getAllZoneStatuses(): List<ParkingZoneStatus> {
        val cache = cacheManager.getCache("parkingZones")
            ?: throw IllegalStateException("Cache 'parkingZones' not found")

        return (cache as CaffeineCache).nativeCache.asMap().values
            .filterIsInstance<ParkingZoneStatus>()
            .toList()
    }
} 
