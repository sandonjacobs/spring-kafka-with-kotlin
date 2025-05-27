package com.example.parking.model

import com.example.parking.model.Zone
import com.example.parking.model.Space
import com.example.parking.model.SpaceStatus
import java.util.UUID

/**
 * Represents a parking garage structure with multiple zones and spaces.
 * This is a realistic representation of a downtown parking garage in a city like Raleigh, NC.
 */
class ParkingGarage {
    companion object {
        // Zone names and their characteristics
        private val ZONE_NAMES = mapOf(
            "P1" to "Level 1 - Main Entry",
            "P2" to "Level 2 - General Parking",
            "P3" to "Level 3 - General Parking",
            "P4" to "Level 4 - General Parking",
            "P5" to "Level 5 - General Parking",
            "P6" to "Level 6 - Reserved Parking",
            "P7" to "Level 7 - Reserved Parking",
            "P8" to "Level 8 - Reserved Parking",
            "P9" to "Level 9 - Reserved Parking",
            "P10" to "Level 10 - Reserved Parking"
        )

        // Spaces per zone (typical for a downtown garage)
        private val SPACES_PER_ZONE = mapOf(
            "P1" to 50,  // Smaller due to entry/exit ramps
            "P2" to 80,
            "P3" to 80,
            "P4" to 80,
            "P5" to 80,
            "P6" to 60,  // Reserved spaces are typically larger
            "P7" to 60,
            "P8" to 60,
            "P9" to 60,
            "P10" to 60
        )

        // Total spaces in the garage
        val TOTAL_SPACES = SPACES_PER_ZONE.values.sum()
    }

    /**
     * Creates a complete parking garage structure with all zones and spaces.
     * @return List of Zone objects representing the entire garage
     */
    fun createGarage(): List<Zone> {
        return ZONE_NAMES.map { (zoneId, zoneName) ->
            createZone(zoneId, zoneName, SPACES_PER_ZONE[zoneId] ?: 0)
        }
    }

    /**
     * Creates a single zone with the specified number of spaces.
     */
    private fun createZone(zoneId: String, zoneName: String, spaceCount: Int): Zone {
        val spaces = (1..spaceCount).map { spaceNumber ->
            Space.newBuilder()
                .setId("${zoneId}-${spaceNumber.toString().padStart(3, '0')}")
                .setZoneId(zoneId)
                .setStatus(SpaceStatus.VACANT)
                .build()
        }

        return Zone.newBuilder()
            .setId(zoneId)
            .setName(zoneName)
            .setTotalSpaces(spaceCount)
            .addAllSpaces(spaces)
            .build()
    }

    /**
     * Gets a random space from the garage.
     */
    fun getRandomSpace(garage: List<Zone>): Space {
        val randomZone = garage.random()
        return randomZone.spacesList.random()
    }

    /**
     * Gets a random vacant space from the garage.
     */
    fun getRandomVacantSpace(garage: List<Zone>): Space? {
        return garage.flatMap { it.spacesList }
            .filter { it.status == SpaceStatus.VACANT }
            .randomOrNull()
    }
} 