package com.ride_service.config;


import com.ride_service.dto.DriverResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "driver-service")
public interface DriverClient {

    @GetMapping("/drivers/available")
    List<DriverResponse> getAvailableDrivers();

    @PutMapping("/drivers/{id}/availability")
    void updateAvailability(
            @PathVariable("id") Long id,
            @RequestParam("available") boolean available);
}