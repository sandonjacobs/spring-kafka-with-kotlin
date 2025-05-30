package com.example.parking.config

import com.example.parking.model.ParkingEvent
import com.example.parking.model.ZoneOccupancy
import io.confluent.kafka.streams.serdes.protobuf.KafkaProtobufSerde
import org.apache.kafka.common.serialization.Serde
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.test.context.TestPropertySource

@Configuration
@Profile("test")
@TestPropertySource(properties = ["kafka.schema-registry.url=mock://"])
class TestSerdeConfig {
    @Bean
    @Primary
    open fun parkingEventSerde(): Serde<ParkingEvent> {
        val serde = KafkaProtobufSerde(ParkingEvent::class.java)
        serde.configure(
            mapOf(
                "schema.registry.url" to "mock://",
                "specific.protobuf.value.type" to ParkingEvent::class.java.name
            ),
            false
        )
        return serde
    }

    @Bean
    @Primary
    open fun zoneOccupancySerde(): Serde<ZoneOccupancy> {
        val serde = KafkaProtobufSerde(ZoneOccupancy::class.java)
        serde.configure(
            mapOf(
                "schema.registry.url" to "mock://",
                "specific.protobuf.value.type" to ZoneOccupancy::class.java.name
            ),
            false
        )
        return serde
    }
} 