package com.example.parking.producer

import com.example.parking.config.KafkaProperties
import com.example.parking.model.ParkingEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class ParkingEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, ParkingEvent>,
    private val kafkaProperties: KafkaProperties
) {
    private val log = LoggerFactory.getLogger(ParkingEventProducer::class.java)

    fun send(event: ParkingEvent) {
        kafkaTemplate.send(kafkaProperties.producer.topic, event.spaceId, event)
        log.info("Sent event to Kafka: $event")
    }
} 