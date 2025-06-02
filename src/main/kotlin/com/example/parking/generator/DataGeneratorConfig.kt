package com.example.parking.generator

import com.example.parking.model.ParkingEvent
import com.example.parking.model.ParkingGarage
import com.example.parking.model.ParkingZone
import com.example.parking.model.ZoneOccupancy
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Configuration class for setting up the data generation components.
 * Configures Kafka producers and the parking garage model.
 *
 * @property bootstrapServers Kafka bootstrap servers configuration
 * @property schemaRegistryUrl URL of the Schema Registry for Protobuf serialization
 */
@Configuration
@EnableScheduling
class DataGeneratorConfig(
    @Value(value = "\${spring.kafka.bootstrap-servers}") private val bootstrapServers: String,
    @Value(value = "\${spring.kafka.properties.[schema.registry.url]}") private val schemaRegistryUrl: String
) {
    /**
     * Creates a sample parking garage with predefined zones.
     *
     * @return A configured ParkingGarage instance
     */
    @Bean
    fun parkingGarage(): ParkingGarage {
        return ParkingGarage(
            id = "main-garage",
            name = "Main Parking Garage",
            totalSpaces = 100,
            zones = listOf(
                ParkingZone("A", "Zone A", 30, occupiedSpaces = 25),
                ParkingZone("B", "Zone B", 40, occupiedSpaces = 40),
                ParkingZone("C", "Zone C", 30, occupiedSpaces = 28)
            )
        )
    }

    /**
     * Creates a Kafka producer factory for ParkingEvent messages.
     *
     * @return A configured ProducerFactory for ParkingEvent messages
     */
    @Bean
    fun parkingEventProducerFactory(): ProducerFactory<String, ParkingEvent> {
        val configProps = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to KafkaProtobufSerializer::class.java,
            "schema.registry.url" to schemaRegistryUrl
        )
        return DefaultKafkaProducerFactory(configProps)
    }

    /**
     * Creates a Kafka template for sending ParkingEvent messages.
     *
     * @param producerFactory The producer factory to use
     * @return A configured KafkaTemplate for ParkingEvent messages
     */
    @Bean
    fun parkingEventKafkaTemplate(producerFactory: ProducerFactory<String, ParkingEvent>): KafkaTemplate<String, ParkingEvent> {
        return KafkaTemplate(producerFactory)
    }

    /**
     * Creates a Kafka producer factory for ZoneOccupancy messages.
     *
     * @return A configured ProducerFactory for ZoneOccupancy messages
     */
    @Bean
    fun zoneOccupancyProducerFactory(): ProducerFactory<String, ZoneOccupancy> {
        val configProps = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to KafkaProtobufSerializer::class.java,
            "schema.registry.url" to schemaRegistryUrl
        )
        return DefaultKafkaProducerFactory(configProps)
    }

    /**
     * Creates a Kafka template for sending ZoneOccupancy messages.
     *
     * @param producerFactory The producer factory to use
     * @return A configured KafkaTemplate for ZoneOccupancy messages
     */
    @Bean
    fun zoneOccupancyKafkaTemplate(producerFactory: ProducerFactory<String, ZoneOccupancy>): KafkaTemplate<String, ZoneOccupancy> {
        return KafkaTemplate(producerFactory)
    }
}
