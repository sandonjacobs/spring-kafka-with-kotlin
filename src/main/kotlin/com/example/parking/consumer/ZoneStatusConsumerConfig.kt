package com.example.parking.consumer

import com.example.parking.model.ZoneOccupancy
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory

/**
 * Configuration class for setting up the Kafka consumer for zone status updates.
 * Configures the consumer factory and listener container factory for processing ZoneOccupancy messages.
 *
 * @property bootstrapServers Kafka bootstrap servers configuration
 * @property schemaRegistryUrl URL of the Schema Registry for Protobuf deserialization
 */
@Configuration
class ZoneStatusConsumerConfig(
    @Value(value = "\${spring.kafka.bootstrap-servers}") private val bootstrapServers: String,
    @Value(value = "\${spring.kafka.properties.[schema.registry.url]}") private val schemaRegistryUrl: String
) {

    /**
     * Creates a Kafka consumer factory for ZoneOccupancy messages.
     *
     * @return A configured ConsumerFactory for ZoneOccupancy messages
     */
    @Bean
    fun consumerFactory(): ConsumerFactory<String, ZoneOccupancy> {
        val configProps = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to KafkaProtobufDeserializer::class.java,
            "schema.registry.url" to schemaRegistryUrl,
            "specific.protobuf.value.type" to ZoneOccupancy::class.java.name
        )
        return DefaultKafkaConsumerFactory(configProps)
    }

    /**
     * Creates a Kafka listener container factory for processing ZoneOccupancy messages.
     *
     * @return A configured ConcurrentKafkaListenerContainerFactory
     */
    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, ZoneOccupancy> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, ZoneOccupancy>()
        factory.consumerFactory = consumerFactory()
        return factory
    }
}
