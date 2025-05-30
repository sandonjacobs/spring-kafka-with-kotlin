package com.example.parking.controller

import com.example.parking.service.ParkingZoneCacheService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Zone Controller")
class ZoneController(
    private val cacheService: ParkingZoneCacheService
) {
    private val log = LoggerFactory.getLogger(javaClass)

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
}