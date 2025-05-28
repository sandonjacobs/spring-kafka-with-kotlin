package com.example.parking.producer

import com.example.parking.config.KafkaProperties
import com.example.parking.model.ZoneOccupancy
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class ZoneOccupancyProducer(
    private val kafkaTemplate: KafkaTemplate<String, ZoneOccupancy>,
    private val kafkaProperties: KafkaProperties
) {
    private val log = LoggerFactory.getLogger(ZoneOccupancyProducer::class.java)

    fun send(zoneId: String, occupancy: ZoneOccupancy) {
        kafkaTemplate.send(kafkaProperties.producer.occupancyTopic, zoneId, occupancy)
        log.info("Sent occupancy update to Kafka for zone {}: {}", zoneId, occupancy)
    }
} 