package com.example.parking.producer

import com.example.parking.config.KafkaProperties
import com.example.parking.model.ParkingEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class ParkingEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, com.google.protobuf.GeneratedMessageV3>,
    private val kafkaProperties: KafkaProperties
) {
    private val log = LoggerFactory.getLogger(ParkingEventProducer::class.java)

    fun send(event: ParkingEvent) {
        kafkaTemplate.send(kafkaProperties.producer.parkingEventsTopic, event.spaceId, event)
        log.info("Sent parking event to Kafka: {}", event)
    }
} 