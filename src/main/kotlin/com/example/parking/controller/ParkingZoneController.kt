package com.example.parking.controller

import com.example.parking.model.ParkingGarage
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ParkingZoneController(
    private val parkingGarage: ParkingGarage
) {
    @GetMapping("/")
    fun index(model: Model): String {
        model.addAttribute("zones", parkingGarage.zones)
        return "index"
    }
} 