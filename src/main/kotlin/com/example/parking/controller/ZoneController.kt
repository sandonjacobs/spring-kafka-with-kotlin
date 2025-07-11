package com.example.parking.controller

import com.example.parking.service.ParkingZoneCacheService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import com.example.parking.model.ParkingZoneEvent

/**
 * REST controller for managing parking zone status information.
 * Provides endpoints for retrieving the current status of parking zones.
 *
 * @property cacheService Service for caching zone status information
 */
@RestController
@Tag(name = "Zone Controller")
class ZoneController(
    private val cacheService: ParkingZoneCacheService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    data class ZoneStatusDTO(
        val zoneId: String,
        val occupiedSpots: Int,
        val availableSpots: Int,
        val lastUpdated: String?
    )

    /**
     * Retrieves the current status of all parking zones.
     * Returns a list of zone statuses from the cache, or a 204 No Content response if no zones are found.
     *
     * @return ResponseEntity containing a list of zone statuses or an empty response
     */
    @Operation(summary = "Get all zone statuses")
    @GetMapping("/api/zones", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllZoneStatuses(): ResponseEntity<out List<Any>?> {
        val statuses = cacheService.getAllZoneStatuses()
        log.info("Retrieved {} zone statuses from cache", statuses.size)
        if (statuses.isEmpty()) {
            return ResponseEntity(HttpStatus.NO_CONTENT)
        } else {
            return ResponseEntity.ok().body(statuses)
        }
    }

    @GetMapping("/api/zones/events", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getRecentZoneEvents(): ResponseEntity<List<ParkingZoneEvent>> {
        return ResponseEntity.ok(cacheService.getRecentEvents())
    }

    @GetMapping("/api/zones/{zoneId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getZoneStatus(@PathVariable zoneId: String): ResponseEntity<Any> {
        val status = cacheService.getZoneStatus(zoneId)
        return if (status != null) {
            ResponseEntity.ok(
                ZoneStatusDTO(
                    zoneId = status.zoneId,
                    occupiedSpots = status.occupiedSpots,
                    availableSpots = status.availableSpots,
                    lastUpdated = status.lastUpdated.toString()
                )
            )
        } else {
            ResponseEntity.notFound().build()
        }
    }
}