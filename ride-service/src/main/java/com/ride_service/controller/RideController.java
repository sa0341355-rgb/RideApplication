package com.ride_service.controller;



import org.springframework.web.bind.annotation.*;

import com.ride_service.config.DriverClient;
import com.ride_service.dto.DriverResponse;
import com.ride_service.location.LocationIQRouteResponse;
import com.ride_service.location.LocationIqService;
import com.ride_service.modal.Ride;
import com.ride_service.repository.RideRepository;
import com.ride_service.service.DriverMatchingService;
import com.ride_service.service.RideService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor 
public class RideController {

    private final RideRepository rideRepository;
    private final DriverClient driverClient;
    private final DriverMatchingService driverMatchingService;
    private final RideService rideService;
    private final LocationIqService locationIqService;




@GetMapping("/route")
public LocationIQRouteResponse getRoute(
        @RequestParam Double pickupLatitude,
        @RequestParam Double pickupLongitude,
        @RequestParam Double dropLatitude,
        @RequestParam Double dropLongitude) {

    return locationIqService.getRoute(
            pickupLatitude,
            pickupLongitude,
            dropLatitude,
            dropLongitude
    );
}



  
@PostMapping
public Ride createRide(
        @RequestParam Long riderId,
        @RequestParam String pickupLocation,
        @RequestParam String dropLocation,
        @RequestParam Double pickupLatitude,
        @RequestParam Double pickupLongitude,
        @RequestParam Double fare) {

    return rideService.createRide(
            riderId,
            pickupLocation,
            dropLocation,
            pickupLatitude,
            pickupLongitude,
            fare);
}
    @GetMapping
    public List<Ride> getAllRides() {

        return rideRepository.findAll();
    }

    @GetMapping("/{id}")
    public Ride getRide(@PathVariable Long id) {

        return rideRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Ride not found"));
    }

    @GetMapping("/nearest-driver")
    public DriverResponse findNearestDriver(
        @RequestParam double latitude,
        @RequestParam double longitude) {

    return driverMatchingService.findNearestDriver(
            latitude,
            longitude);
    }

    @GetMapping("/available-drivers")
    public List<DriverResponse> getAvailableDrivers() {

      return driverClient.getAvailableDrivers();
     }
}
