package com.example.parking

import com.example.parking.generator.ParkingEventGenerator
import com.example.parking.model.ParkingGarage
import com.example.parking.model.ZoneOccupancy
import com.example.parking.producer.ParkingEventProducer
import com.example.parking.producer.ZoneOccupancyProducer
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.time.LocalDateTime

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
    open fun run(
        generator: ParkingEventGenerator,
        parkingEventProducer: ParkingEventProducer,
        occupancyProducer: ZoneOccupancyProducer
    ): CommandLineRunner {
        return CommandLineRunner {
            log.info("Starting parking event simulation...")
            
            // First, send initial zone occupancy state
            log.info("Sending initial zone occupancy state...")
            generator.generateInitialZoneOccupancies().forEach { (zoneId, occupancy): Pair<String, ZoneOccupancy> ->
                occupancyProducer.send(zoneId, occupancy)
            }
            log.info("Initial zone occupancy state sent.")
            
            // Then start generating random events
            repeat(simulationProperties.totalEvents) { i ->
                val event = if (Math.random() < simulationProperties.entryProbability) {
                    generator.generateEntryEvent()
                } else {
                    generator.generateExitEvent()
                }
                
                if (event != null) {
                    parkingEventProducer.send(event)
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