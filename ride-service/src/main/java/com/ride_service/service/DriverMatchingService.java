package com.ride_service.service;


import com.ride_service.config.DriverClient;
import com.ride_service.dto.DriverResponse;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class DriverMatchingService {

    private final DriverClient driverClient;

    public DriverMatchingService(DriverClient driverClient) {
        this.driverClient = driverClient;
    }

    public DriverResponse findNearestDriver(
            double pickupLatitude,
            double pickupLongitude) {

        List<DriverResponse> drivers =
                driverClient.getAvailableDrivers();

        if (drivers.isEmpty()) {
            throw new RuntimeException(
                    "No available drivers found");
        }

        return drivers.stream()
                .filter(driver ->
                        driver.getLatitude() != null &&
                        driver.getLongitude() != null)
                .min(Comparator.comparingDouble(driver ->
                        calculateDistance(
                                pickupLatitude,
                                pickupLongitude,
                                driver.getLatitude(),
                                driver.getLongitude()
                        )))
                .orElseThrow(() ->
                        new RuntimeException(
                                "No driver with location found"));
    }

    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        double earthRadius = 6371;

        double latDistance =
                Math.toRadians(lat2 - lat1);

        double lonDistance =
                Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(latDistance / 2)
                        * Math.sin(latDistance / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2)
                        * Math.sin(lonDistance / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a));

        return earthRadius * c;
    }
}