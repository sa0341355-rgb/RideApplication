package com.ride_service.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiderResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
}
