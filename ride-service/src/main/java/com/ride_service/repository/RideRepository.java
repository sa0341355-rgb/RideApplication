package com.ride_service.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.ride_service.modal.Ride;

public interface RideRepository extends JpaRepository<Ride, Long> {
}