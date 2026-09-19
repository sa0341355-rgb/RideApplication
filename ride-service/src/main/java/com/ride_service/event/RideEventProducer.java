package com.ride_service.event;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RideEventProducer {

    private static final String TOPIC = "ride-events";

    private final KafkaTemplate<String, RideCreateEvent> kafkaTemplate;

    public RideEventProducer(
            KafkaTemplate<String, RideCreateEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishRideCreated(RideCreateEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.getRideId().toString(),
                event
        );
    }
}