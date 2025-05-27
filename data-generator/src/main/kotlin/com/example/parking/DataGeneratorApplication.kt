package com.example.parking

import com.example.parking.generator.ParkingEventGenerator
import com.example.parking.model.ParkingGarage
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import java.time.Duration
import java.time.LocalDateTime
import kotlin.concurrent.thread

@ConfigurationProperties(prefix = "simulation")
data class SimulationProperties(
    val totalEvents: Int,
    val entryProbability: Double,
    val delay: Long,
    val timeOfDay: TimeOfDayProperties,
    val eventRate: EventRateProperties,
    val initialOccupancy: Double
)

data class TimeOfDayProperties(
    var enabled: Boolean = true,
    var startHour: Int = 6,
    var endHour: Int = 22
)

data class EventRateProperties(
    var peakRate: Int = 10,
    var offPeakRate: Int = 2,
    var peakHours: List<Int> = listOf(8, 17)
)

@SpringBootApplication
@EnableConfigurationProperties(SimulationProperties::class)
open class DataGeneratorApplication {
    @Bean
    open fun runner(properties: SimulationProperties): ApplicationRunner = ApplicationRunner {
        val garage = ParkingGarage()
        val generator = ParkingEventGenerator(garage, properties.initialOccupancy)

        println("Starting parking event simulation...")
        repeat(properties.totalEvents) { i ->
            val event = if (Math.random() < properties.entryProbability) {
                generator.generateEntryEvent()
            } else {
                generator.generateExitEvent()
            }
            if (event != null) {
                println("Event #${i + 1}: $event")
            }
            val currentHour = LocalDateTime.now().hour
            val isPeakHour = properties.eventRate.peakHours.contains(currentHour)
            val rate = if (isPeakHour) properties.eventRate.peakRate else properties.eventRate.offPeakRate
            val delay = 1000L / rate
            Thread.sleep(delay)
        }
        println("Simulation complete.")
    }

    @Bean
    open fun parkingEventGenerator(properties: SimulationProperties): ParkingEventGenerator {
        val garage = ParkingGarage()
        return ParkingEventGenerator(garage, properties.initialOccupancy)
    }
}

fun main(args: Array<String>) {
    runApplication<DataGeneratorApplication>(*args)
} 