package com.example.parking.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "kafka")
data class KafkaProperties(
    val bootstrapServers: String = "localhost:9092",
    val schemaRegistry: SchemaRegistryProperties = SchemaRegistryProperties(),
    val producer: ProducerProperties = ProducerProperties()
)

data class SchemaRegistryProperties(
    val url: String = "http://localhost:8081"
)

data class ProducerProperties(
    val topic: String = "parking-events-proto"
) 