package agenticridebooking.driver_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import agenticridebooking.driver_service.modal.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> {
}