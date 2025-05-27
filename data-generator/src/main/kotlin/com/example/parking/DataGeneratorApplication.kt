package com.example.parking

import com.example.parking.generator.ParkingEventGenerator
import com.example.parking.model.ParkingGarage
import com.example.parking.producer.ParkingEventProducer
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
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
open class DataGeneratorApplication(private val simulationProperties: SimulationProperties) {

    companion object {
        private val log = LoggerFactory.getLogger(DataGeneratorApplication::class.java)
    }

    @Bean
    open fun parkingEventGenerator(): ParkingEventGenerator {
        val garage = ParkingGarage()
        return ParkingEventGenerator(garage, simulationProperties.initialOccupancy)
    }

    @Bean
    open fun run(generator: ParkingEventGenerator, producer: ParkingEventProducer): CommandLineRunner {
        return CommandLineRunner {
            log.info("Starting parking event simulation...")
            
            repeat(simulationProperties.totalEvents) { i ->
                val event = if (Math.random() < simulationProperties.entryProbability) {
                    generator.generateEntryEvent()
                } else {
                    generator.generateExitEvent()
                }
                
                if (event != null) {
                    producer.send(event)
                    log.info("Event #${i + 1} sent to Kafka: $event")
                }
                
                val currentHour = LocalDateTime.now().hour
                val isPeakHour = simulationProperties.eventRate.peakHours.contains(currentHour)
                val rate = if (isPeakHour) simulationProperties.eventRate.peakRate else simulationProperties.eventRate.offPeakRate
                val delay = 1000L / rate
                Thread.sleep(delay)
            }
            
            log.info("Simulation complete.")
        }
    }
}

fun main(args: Array<String>) {
    runApplication<DataGeneratorApplication>(*args)
} 