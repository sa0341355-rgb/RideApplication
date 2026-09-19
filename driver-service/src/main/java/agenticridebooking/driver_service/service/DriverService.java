package agenticridebooking.driver_service.service;


import org.springframework.stereotype.Service;

import agenticridebooking.driver_service.dto.DriverLocationRequest;
import agenticridebooking.driver_service.modal.Driver;
import agenticridebooking.driver_service.repository.DriverRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class DriverService {

    private final DriverRepository driverRepository;

  

    public Driver updateLocation(
            Long driverId,
            DriverLocationRequest request) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() ->
                        new RuntimeException("Driver not found"));

        if (request.getLatitude() == null ||
            request.getLongitude() == null) {

            throw new RuntimeException(
                    "Latitude and longitude are required");
        }

        driver.setLatitude(request.getLatitude());
        driver.setLongitude(request.getLongitude());

        return driverRepository.save(driver);
    }

    public Driver updateAvailability(
            Long driverId,
            boolean available) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() ->
                        new RuntimeException("Driver not found"));

        driver.setAvailable(available);

        return driverRepository.save(driver);
    }
}
