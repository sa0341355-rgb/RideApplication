package agenticridebooking.driver_service.controller;

import org.springframework.web.bind.annotation.*;

import agenticridebooking.driver_service.dto.DriverLocationRequest;
import agenticridebooking.driver_service.modal.Driver;
import agenticridebooking.driver_service.repository.DriverRepository;
import agenticridebooking.driver_service.service.DriverService;

import java.util.List;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    private final DriverRepository driverRepository;
    private final DriverService driverService;

    public DriverController(
            DriverRepository driverRepository,
            DriverService driverService) {

        this.driverRepository = driverRepository;
        this.driverService = driverService;
    }

    @PostMapping
    public Driver createDriver(
            @RequestBody Driver driver) {

        return driverRepository.save(driver);
    }

    @GetMapping
    public List<Driver> getAllDrivers() {

        return driverRepository.findAll();
    }

    @GetMapping("/{id}")
    public Driver getDriver(
            @PathVariable Long id) {

        return driverRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Driver not found"));
    }

    @PutMapping("/{id}/location")
    public Driver updateLocation(
            @PathVariable Long id,
            @RequestBody DriverLocationRequest request) {

        return driverService.updateLocation(id, request);
    }

    @PutMapping("/{id}/availability")
    public Driver updateAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {

        return driverService.updateAvailability(
                id, available);
    }

    @DeleteMapping("/{id}")
    public String deleteDriver(
            @PathVariable Long id) {

        if (!driverRepository.existsById(id)) {
            throw new RuntimeException("Driver not found");
        }

        driverRepository.deleteById(id);

        return "Driver deleted successfully";
    }
    @GetMapping("/available")
public List<Driver> getAvailableDrivers() {
    return driverRepository.findAll()
            .stream()
            .filter(Driver::isAvailable)
            .toList();
}
}