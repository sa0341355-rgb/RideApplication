package com.ride_service.location;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class LocationIqService {

    private final WebClient webClient;

    @Value("${locationiq.api-key}")
    private String apiKey;

    @Value("${locationiq.base-url}")
    private String baseUrl;

    public LocationIqService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public LocationIQRouteResponse getRoute(
            double pickupLatitude,
            double pickupLongitude,
            double dropLatitude,
            double dropLongitude) {

        String coordinates =
                pickupLongitude + "," + pickupLatitude
                        + ";"
                        + dropLongitude + "," + dropLatitude;

        String url = baseUrl
                + "/directions/driving/"
                + coordinates;

        System.out.println("LocationIQ URL: " + url);

        return webClient.get()
                .uri(url + "?key=" + apiKey
                        + "&overview=full"
                        + "&steps=false")
                .retrieve()
                .bodyToMono(LocationIQRouteResponse.class)
                .block();
    }
}