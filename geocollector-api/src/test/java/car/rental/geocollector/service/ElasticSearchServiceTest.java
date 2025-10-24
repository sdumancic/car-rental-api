package car.rental.geocollector.service;

import car.rental.geocollector.dto.VehicleLocationEvent;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.Random;

@QuarkusTest
class ElasticSearchServiceTest {

    @Inject
    ElasticSearchService elasticSearchService;

    @Test
    void sendTenVehicleLocationEvents() {
        for (int i = 0; i < 10; i++) {
            VehicleLocationEvent event = new VehicleLocationEvent(
                    null,
                    "reservation-" + i,
                    "vehicle-" + i,
                    "user-" + i,
                    Instant.now(),
                    45.0 + i,
                    15.0 + i
            );
            elasticSearchService.sendVehicleLocationEvent(event);
        }
        // Optionally, add assertions or await logic if you want to verify delivery
    }

    @Test
    void sendTwentyVehicleLocationEventsWithMovement() throws InterruptedException {
        double startLat = 46.38444;
        double startLon = 16.43389;
        double lat = startLat;
        double lon = startLon;
        double delta = 0.0009; // ~100 meters in latitude/longitude
        for (int i = 0; i < 20; i++) {
            VehicleLocationEvent event = new VehicleLocationEvent(
                    null,
                    "test-reservation",
                    "test-vehicle",
                    "test-user",
                    java.time.Instant.now(),
                    lat,
                    lon
            );
            elasticSearchService.sendVehicleLocationEvent(event);
            lat += delta;
            lon += delta;
            Thread.sleep(1000); // 1 second between events
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 10, 20})
        // Example values for N
    void sendVehicleLocationEventsForMultipleVehicles(int n) throws InterruptedException {
        double startLat = 46.38444;
        double startLon = 16.43389;
        Random random = new Random();
        double metersToDegrees = 0.000009; // Approximate conversion for latitude/longitude
        int eventsPerVehicle = 10;
        for (int vehicleIdx = 0; vehicleIdx < n; vehicleIdx++) {
            double lat = startLat + vehicleIdx * metersToDegrees * 100; // Spread starting points
            double lon = startLon + vehicleIdx * metersToDegrees * 100;
            for (int eventIdx = 0; eventIdx < eventsPerVehicle; eventIdx++) {
                double latDelta = (random.nextDouble() * 200) * metersToDegrees * (random.nextBoolean() ? 1 : -1);
                double lonDelta = (random.nextDouble() * 200) * metersToDegrees * (random.nextBoolean() ? 1 : -1);
                lat += latDelta;
                lon += lonDelta;
                VehicleLocationEvent event = new VehicleLocationEvent(
                        null,
                        "reservation-" + vehicleIdx,
                        "vehicle-" + vehicleIdx,
                        "user-" + vehicleIdx,
                        java.time.Instant.now(),
                        lat,
                        lon
                );
                elasticSearchService.sendVehicleLocationEvent(event);
                Thread.sleep(2000); // 2 seconds between events
            }
        }
    }
}
