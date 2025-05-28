package com.example.parking.config

import com.google.protobuf.GeneratedMessageV3
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
@EnableConfigurationProperties(KafkaProperties::class)
open class KafkaProducerConfig(private val kafkaProperties: KafkaProperties) {

    @Bean
    open fun producerFactory(): ProducerFactory<String, GeneratedMessageV3> {
        val configProps = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to KafkaProtobufSerializer::class.java,
            "schema.registry.url" to kafkaProperties.schemaRegistry.url
        )
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    open fun kafkaTemplate(): KafkaTemplate<String, GeneratedMessageV3> =
        KafkaTemplate(producerFactory())
} 