package com.example.parking.generator

import com.example.parking.model.ParkingGarage
import com.example.parking.model.SpaceStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ParkingGarageTest {
    private val garage = ParkingGarage()

    @Test
    fun `garage has correct number of zones`() {
        val zones = garage.createGarage()
        assertEquals(10, zones.size, "Garage should have 10 zones")
    }

    @Test
    fun `garage has correct total number of spaces`() {
        val zones = garage.createGarage()
        val totalSpaces = zones.sumOf { it.spacesCount }
        assertEquals(ParkingGarage.TOTAL_SPACES, totalSpaces, "Garage should have ${ParkingGarage.TOTAL_SPACES} total spaces")
    }

    @Test
    fun `each zone has correct number of spaces`() {
        val zones = garage.createGarage()
        val zoneSpaces = zones.associate { it.id to it.spacesCount }
        
        assertEquals(50, zoneSpaces["P1"], "Level 1 should have 50 spaces")
        assertEquals(80, zoneSpaces["P2"], "Level 2 should have 80 spaces")
        assertEquals(80, zoneSpaces["P3"], "Level 3 should have 80 spaces")
        assertEquals(80, zoneSpaces["P4"], "Level 4 should have 80 spaces")
        assertEquals(80, zoneSpaces["P5"], "Level 5 should have 80 spaces")
        assertEquals(60, zoneSpaces["P6"], "Level 6 should have 60 spaces")
        assertEquals(60, zoneSpaces["P7"], "Level 7 should have 60 spaces")
        assertEquals(60, zoneSpaces["P8"], "Level 8 should have 60 spaces")
        assertEquals(60, zoneSpaces["P9"], "Level 9 should have 60 spaces")
        assertEquals(60, zoneSpaces["P10"], "Level 10 should have 60 spaces")
    }

    @Test
    fun `all spaces are initially vacant`() {
        val zones = garage.createGarage()
        val allSpaces = zones.flatMap { it.spacesList }
        
        assertTrue(allSpaces.all { it.status == SpaceStatus.VACANT }, 
            "All spaces should be initially vacant")
    }

    @Test
    fun `space IDs follow correct format`() {
        val zones = garage.createGarage()
        val allSpaces = zones.flatMap { it.spacesList }
        
        allSpaces.forEach { space ->
            println("Space ID: ${space.id}")
            assertTrue(space.id.matches(Regex("P\\d{1,2}-\\d{3}")), 
                "Space ID ${space.id} should match format P{level}-{spaceNumber}")
        }
    }

    @Test
    fun `getRandomSpace returns a valid space`() {
        val zones = garage.createGarage()
        val space = garage.getRandomSpace(zones)
        
        assertNotNull(space, "Random space should not be null")
        assertTrue(zones.any { it.spacesList.contains(space) }, 
            "Random space should belong to one of the zones")
    }

    @Test
    fun `getRandomVacantSpace returns a vacant space`() {
        val zones = garage.createGarage()
        val space = garage.getRandomVacantSpace(zones)
        
        assertNotNull(space, "Random vacant space should not be null")
        assertEquals(SpaceStatus.VACANT, space!!.status, 
            "Random space should be vacant")
    }

    @Test
    fun `zone names are correctly set`() {
        val zones = garage.createGarage()
        val zoneNames = zones.associate { it.id to it.name }
        
        assertEquals("Level 1 - Main Entry", zoneNames["P1"])
        assertEquals("Level 2 - General Parking", zoneNames["P2"])
        assertEquals("Level 3 - General Parking", zoneNames["P3"])
        assertEquals("Level 4 - General Parking", zoneNames["P4"])
        assertEquals("Level 5 - General Parking", zoneNames["P5"])
        assertEquals("Level 6 - Reserved Parking", zoneNames["P6"])
        assertEquals("Level 7 - Reserved Parking", zoneNames["P7"])
        assertEquals("Level 8 - Reserved Parking", zoneNames["P8"])
        assertEquals("Level 9 - Reserved Parking", zoneNames["P9"])
        assertEquals("Level 10 - Reserved Parking", zoneNames["P10"])
    }
} 