package com.example.parking.controller

import com.example.parking.model.ParkingZoneCacheStatus
import com.example.parking.service.ParkingZoneCacheService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ParkingZoneController(
    private val parkingZoneCacheService: ParkingZoneCacheService
) {
    @GetMapping("/")
    fun index(model: Model): String {
        model.addAttribute("zones", parkingZoneCacheService.getAllZoneStatuses())
        return "index"
    }
} 