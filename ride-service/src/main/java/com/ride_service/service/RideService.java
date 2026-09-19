package com.ride_service.service;

import com.ride_service.config.DriverClient;
import com.ride_service.config.RiderClient;
import com.ride_service.dto.DriverResponse;
import com.ride_service.dto.RiderResponse;
import com.ride_service.event.RideCreateEvent;
import com.ride_service.event.RideEventProducer;
import com.ride_service.modal.Ride;
import com.ride_service.modal.RideStatus;
import com.ride_service.repository.RideRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor 
public class RideService {

    private final RideRepository rideRepository;
    private final RiderClient riderClient;
    private final DriverMatchingService driverMatchingService;
    private final DriverClient driverClient;
    private final RideEventProducer rideEventProducer;

   

  public Ride createRide(
        Long riderId,
        String pickupLocation,
        String dropLocation,
        Double pickupLatitude,
        Double pickupLongitude,
        Double fare) {

    // 1. Validate rider
    RiderResponse rider = riderClient.getRider(riderId);

    if (rider == null) {
        throw new RuntimeException("Rider not found");
    }

    // 2. Find nearest available driver
    DriverResponse driver =
            driverMatchingService.findNearestDriver(
                    pickupLatitude,
                    pickupLongitude);

    // 3. Mark driver unavailable
    driverClient.updateAvailability(driver.getId(), false);

    // 4. Create ride
    Ride ride = new Ride();

    ride.setRiderId(riderId);
    ride.setDriverId(driver.getId());

    ride.setPickupLocation(pickupLocation);
    ride.setDropLocation(dropLocation);

    ride.setFare(fare);

    ride.setStatus(RideStatus.DRIVER_ASSIGNED);

    // 5. Save ride
    Ride savedRide = rideRepository.save(ride);

    // 6. Create Kafka event
    RideCreateEvent event = new RideCreateEvent(
            savedRide.getId(),
            savedRide.getRiderId(),
            savedRide.getDriverId(),
            savedRide.getPickupLocation(),
            savedRide.getDropLocation(),
            savedRide.getFare(),
            savedRide.getStatus().name()
    );

    // 7. Publish event to Kafka
    rideEventProducer.publishRideCreated(event);

    return savedRide;
}
}
