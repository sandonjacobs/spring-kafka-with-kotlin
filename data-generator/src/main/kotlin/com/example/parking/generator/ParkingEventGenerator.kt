package com.example.parking.generator

import com.example.parking.model.*
import java.time.Instant
import java.util.UUID
import kotlin.random.Random

class ParkingEventGenerator(
    private val garage: ParkingGarage,
    private val initialOccupancy: Double = 0.0
) {
    private val state: MutableMap<String, Space> = mutableMapOf()
    private val zones: List<Zone> = garage.createGarage()
    private val random = Random(System.currentTimeMillis())

    init {
        initializeGarage()
    }

    /**
     * Initialize the garage with some occupied spaces based on the initialOccupancy percentage.
     */
    private fun initializeGarage() {
        if (initialOccupancy <= 0.0) return

        // Get all spaces from all zones
        val allSpaces = zones.flatMap { it.spacesList }
        val spacesToOccupy = (allSpaces.size * initialOccupancy).toInt()

        // Randomly select spaces to occupy
        allSpaces.shuffled(random).take(spacesToOccupy).forEach { space ->
            val vehicle = randomVehicle()
            val now = Instant.now().toEpochMilli()

            val occupiedSpace = space.toBuilder()
                .setStatus(SpaceStatus.OCCUPIED)
                .setVehicle(vehicle)
                .setOccupiedAt(now)
                .build()
            state[occupiedSpace.id] = occupiedSpace
        }
    }

    /**
     * Simulate a vehicle entering the garage.
     * Returns the generated ParkingEvent, or null if no vacant space is available.
     */
    fun generateEntryEvent(): ParkingEvent? {
        val vacantSpace = garage.getRandomVacantSpace(zones) ?: return null
        val vehicle = randomVehicle()
        val now = Instant.now().toEpochMilli()

        // Mark the space as occupied in our state
        val occupiedSpace = vacantSpace.toBuilder()
            .setStatus(SpaceStatus.OCCUPIED)
            .setVehicle(vehicle)
            .setOccupiedAt(now)
            .build()
        state[occupiedSpace.id] = occupiedSpace

        return ParkingEvent.newBuilder()
            .setEventId(UUID.randomUUID().toString())
            .setSpaceId(occupiedSpace.id)
            .setZoneId(occupiedSpace.zoneId)
            .setEventType(EventType.VEHICLE_ENTERED)
            .setVehicle(vehicle)
            .setTimestamp(now)
            .build()
    }

    /**
     * Simulate a vehicle exiting the garage.
     * Returns the generated ParkingEvent, or null if no occupied space is available.
     */
    fun generateExitEvent(): ParkingEvent? {
        val occupiedSpaces = state.values.filter { it.status == SpaceStatus.OCCUPIED }
        if (occupiedSpaces.isEmpty()) return null
        val space = occupiedSpaces.random(random)
        val now = Instant.now().toEpochMilli()
        val vehicle = space.vehicle

        // Mark the space as vacant in our state
        val vacantSpace = space.toBuilder()
            .setStatus(SpaceStatus.VACANT)
            .clearVehicle()
            .setOccupiedAt(0L)
            .build()
        state[vacantSpace.id] = vacantSpace

        return ParkingEvent.newBuilder()
            .setEventId(UUID.randomUUID().toString())
            .setSpaceId(space.id)
            .setZoneId(space.zoneId)
            .setEventType(EventType.VEHICLE_EXITED)
            .setVehicle(vehicle)
            .setTimestamp(now)
            .build()
    }

    /**
     * Generate a random vehicle.
     */
    private fun randomVehicle(): Vehicle {
        val id = UUID.randomUUID().toString()
        val licensePlate = randomLicensePlate()
        val state = listOf("NC", "SC", "VA", "GA", "TN").random(random)
        val type = listOf(
            VehicleType.CAR,
            VehicleType.TRUCK,
            VehicleType.MOTORCYCLE,
            VehicleType.BUS
        ).random(random)
        return Vehicle.newBuilder()
            .setId(id)
            .setLicensePlate(licensePlate)
            .setState(state)
            .setType(type)
            .build()
    }

    private fun randomLicensePlate(): String {
        val letters = (1..3).map { ('A'..'Z').random(random) }.joinToString("")
        val numbers = (1..4).map { ('0'..'9').random(random) }.joinToString("")
        return "$letters-$numbers"
    }

    /**
     * Get the current state of the garage (for testing or reporting).
     */
    fun currentState(): List<Space> = state.values.toList()
} 