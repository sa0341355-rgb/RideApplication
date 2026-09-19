package com.ride_service.location;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LocationIQRouteResponse {

    private String code;

    private List<Route> routes;

    @Getter
    @Setter
    public static class Route {

        private double distance;

        private double duration;

        private String geometry;
    }
}