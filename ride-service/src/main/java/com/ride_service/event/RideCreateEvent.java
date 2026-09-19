package com.ride_service.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideCreateEvent {

    private Long rideId;
    private Long riderId;
    private Long driverId;

    private String pickupLocation;
    private String dropLocation;

    private Double fare;
    private String status;
}
