package agenticridebooking.driver_service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DriverRideEventListener {

    private final ObjectMapper objectMapper;

    public DriverRideEventListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "ride-events",
            groupId = "driver-service-group"
    )
    public void consumeRideCreated(String message) {

        try {

            RideCreatedEvent event =
                    objectMapper.readValue(
                            message,
                            RideCreatedEvent.class
                    );

            System.out.println("=================================");
            System.out.println("Kafka Event Received");
            System.out.println("Ride ID: " + event.getRideId());
            System.out.println("Rider ID: " + event.getRiderId());
            System.out.println("Driver ID: " + event.getDriverId());
            System.out.println("Pickup: " + event.getPickupLocation());
            System.out.println("Drop: " + event.getDropLocation());
            System.out.println("Fare: " + event.getFare());
            System.out.println("Status: " + event.getStatus());
            System.out.println("=================================");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}