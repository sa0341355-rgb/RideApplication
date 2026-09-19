package com.ride_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverResponse {

    private Long id;
    private String name;
    private String phone;
    private String vehicleNumber;
    private String vehicleType;
    private boolean available;
    private Double latitude;
    private Double longitude;
}