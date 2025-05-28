package com.example.parking.generator

import com.example.parking.model.Space
import com.example.parking.model.Zone
import kotlin.random.Random

class ParkingGarage {
    private val random = Random(System.currentTimeMillis())

    /**
     * Create a parking garage with multiple zones and spaces.
     * Each zone has a unique ID and contains multiple parking spaces.
     */
    fun createGarage(): List<Zone> {
        return listOf(
            createZone("A", 50),  // Zone A with 50 spaces
            createZone("B", 75),  // Zone B with 75 spaces
            createZone("C", 100), // Zone C with 100 spaces
            createZone("D", 50)   // Zone D with 50 spaces
        )
    }

    /**
     * Create a zone with the specified number of spaces.
     */
    private fun createZone(zoneId: String, numSpaces: Int): Zone {
        val spaces = (1..numSpaces).map { spaceNumber ->
            Space.newBuilder()
                .setId("$zoneId-$spaceNumber")
                .setZoneId(zoneId)
                .setStatus(com.example.parking.model.SpaceStatus.VACANT)
                .build()
        }

        return Zone.newBuilder()
            .setId(zoneId)
            .addAllSpaces(spaces)
            .build()
    }

    /**
     * Get a random vacant space from the given zones.
     * Returns null if no vacant space is available.
     */
    fun getRandomVacantSpace(zones: List<Zone>): Space? {
        val vacantSpaces = zones.flatMap { it.spacesList }
            .filter { it.status == com.example.parking.model.SpaceStatus.VACANT }
        
        return if (vacantSpaces.isEmpty()) null else vacantSpaces.random(random)
    }
} 