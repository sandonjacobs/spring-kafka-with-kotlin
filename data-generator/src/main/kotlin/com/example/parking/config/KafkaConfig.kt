package com.example.parking.config

import com.example.parking.model.ZoneOccupancy
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
open class KafkaConfig(private val kafkaProperties: KafkaProperties) {

    @Bean
    open fun zoneOccupancyProducerFactory(): ProducerFactory<String, ZoneOccupancy> {
        val configProps = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to KafkaProtobufSerializer::class.java,
            "schema.registry.url" to kafkaProperties.schemaRegistry.url
        )
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    open fun zoneOccupancyKafkaTemplate(): KafkaTemplate<String, ZoneOccupancy> {
        return KafkaTemplate(zoneOccupancyProducerFactory())
    }
} 