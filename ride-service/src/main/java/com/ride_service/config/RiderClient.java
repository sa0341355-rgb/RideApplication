package com.ride_service.config;



import com.ride_service.dto.RiderResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "rider-service")
public interface RiderClient {

    @GetMapping("/riders/{id}")
    RiderResponse getRider(@PathVariable("id") Long id);
}