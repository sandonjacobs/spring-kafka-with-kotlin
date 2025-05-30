package com.example.parking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class ParkingServiceApplication

fun main(args: Array<String>) {
    runApplication<ParkingServiceApplication>(*args)
} 