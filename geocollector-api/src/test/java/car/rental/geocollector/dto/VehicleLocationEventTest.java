package car.rental.geocollector.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VehicleLocationEventTest {
    @Test
    void generateTenEvents() {
        List<VehicleLocationEvent> events = new ArrayList<>();
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
            events.add(event);
        }
        assertEquals(10, events.size());
        // Optionally print events for inspection
        events.forEach(e -> System.out.println(e));
    }
}

