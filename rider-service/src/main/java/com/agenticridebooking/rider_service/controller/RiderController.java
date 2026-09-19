package com.agenticridebooking.rider_service.controller;

import org.springframework.web.bind.annotation.*;

import com.agenticridebooking.rider_service.modal.Rider;
import com.agenticridebooking.rider_service.repository.RiderRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/riders")
@RequiredArgsConstructor 
public class RiderController {

    private final RiderRepository riderRepository;

    

    @PostMapping
    public Rider createRider(@RequestBody Rider rider) {
        return riderRepository.save(rider);
    }

    @GetMapping
    public List<Rider> getAllRiders() {
        return riderRepository.findAll();
    }

    @GetMapping("/{id}")
    public Rider getRider(@PathVariable Long id) {
        return riderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rider not found"));
    }

    @DeleteMapping("/{id}")
    public String deleteRider(@PathVariable Long id) {

        if (!riderRepository.existsById(id)) {
            throw new RuntimeException("Rider not found");
        }

        riderRepository.deleteById(id);

        return "Rider deleted successfully";
    }
}