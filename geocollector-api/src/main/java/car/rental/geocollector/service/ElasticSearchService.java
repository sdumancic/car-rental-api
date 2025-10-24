package car.rental.geocollector.service;

import car.rental.geocollector.dto.VehicleLocationEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@Slf4j
@ApplicationScoped
public class ElasticSearchService {

    @Inject
    @Channel("vehicle-location-events")
    Emitter<VehicleLocationEvent> locationEmitter;

    public void sendVehicleLocationEvent(VehicleLocationEvent event) {
        locationEmitter.send(event)
                .whenComplete((success, throwable) -> {
                    if (throwable != null) {
                        log.error("Failed to send vehicle location for vehicleId {}: {}", event.getVehicleId(), throwable.getMessage());
                    } else {
                        log.info("Location event sent successfully for vehicle {} and reservation {}", event.getVehicleId(), event.getReservationId());
                    }
                });
    }
}
