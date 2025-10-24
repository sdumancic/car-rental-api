package car.rental.geocollector.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class VehicleLocationEvent implements Serializable {

    private UUID id;
    private String reservationId;
    private String vehicleId;
    private String userId;
    private Instant timestamp;
    private double latitude;
    private double longitude;

    public VehicleLocationEvent(UUID id, String reservationId, String vehicleId, String userId, Instant timestamp, double latitude, double longitude) {
        this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
        this.reservationId = reservationId;
        this.vehicleId = vehicleId;
        this.userId = userId;

        if (timestamp != null) {
            this.timestamp = timestamp;
        } else {
            this.timestamp = Instant.now();
        }

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Map<String, Object> toElasticDocument() {
        return Map.of(
                "id", id.toString(),
                "reservationId", reservationId,
                "vehicleId", vehicleId,
                "userId", userId,
                "timestamp", timestamp.toString(),
                "location", Map.of("lat", latitude, "lon", longitude)
        );
    }

}
