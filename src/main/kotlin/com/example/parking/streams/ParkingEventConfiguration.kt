package com.example.parking.streams

import com.example.parking.model.ParkingEvent
import com.example.parking.model.ZoneOccupancy
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration

/**
 * Configuration class for Kafka Streams processing of parking events.
 * Sets up the streams configuration and serialization/deserialization for Protobuf messages.
 *
 * @property bootstrapServers Kafka bootstrap servers configuration
 * @property schemaRegistryUrl URL of the Schema Registry for Protobuf serialization
 */
@Configuration
@EnableKafkaStreams
class ParkingEventConfiguration(
    @Value(value = "\${spring.kafka.bootstrap-servers}") val bootstrapServers: String,
    @Value(value = "\${spring.kafka.properties.[schema.registry.url]}") val schemaRegistryUrl: String) {

    /**
     * Creates the Kafka Streams configuration with necessary properties.
     *
     * @return A configured KafkaStreamsConfiguration instance
     */
    @Bean(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    fun streamsConfig(): KafkaStreamsConfiguration = KafkaStreamsConfiguration(
        baseSchemaRegistryProps() + mapOf<String, Any>(
            StreamsConfig.APPLICATION_ID_CONFIG to "spring-parking-events-app",
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.String()::class.java,
            StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG to KafkaProtobufSerde::class.java
        )
    )

    /**
     * Creates base configuration properties for Schema Registry integration.
     *
     * @return Map of Schema Registry configuration properties
     */
    private fun baseSchemaRegistryProps(): Map<String, Any> =
        mapOf(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to schemaRegistryUrl)

    /**
     * Creates base configuration properties for Serde configuration.
     *
     * @return Map of Serde configuration properties
     */
    private fun baseSerdeProperties(): Map<String, Any> =
        mapOf(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to schemaRegistryUrl)

    /**
     * Creates a Serde for ParkingEvent messages.
     *
     * @return A configured Serde for ParkingEvent messages
     */
    @Bean
    fun parkingEventSerde(): Serde<ParkingEvent> {
        val serde = KafkaProtobufSerde<ParkingEvent>()
        serde.configure(baseSerdeProperties() + mapOf<String, Any>(
            "specific.protobuf.value.type" to ParkingEvent::class.java.name),
            false)
        return serde
    }

    /**
     * Creates a Serde for ZoneOccupancy messages.
     *
     * @return A configured Serde for ZoneOccupancy messages
     */
    @Bean
    fun zoneOccupancySerde(): Serde<ZoneOccupancy> {
        val serde = KafkaProtobufSerde<ZoneOccupancy>()
        serde.configure(baseSerdeProperties() + mapOf<String, Any>(
            "specific.protobuf.value.type" to ZoneOccupancy::class.java.name),
            false)
        return serde
    }

}