package com.example.parking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

/**
 * Main Spring Boot application class for the Parking Service.
 * This application manages parking garage occupancy using Kafka for event streaming.
 */
@SpringBootApplication
@EnableCaching
class ParkingServiceApplication

/**
 * Application entry point.
 *
 * @param args Command line arguments passed to the application
 */
fun main(args: Array<String>) {
    runApplication<ParkingServiceApplication>(*args)
} 