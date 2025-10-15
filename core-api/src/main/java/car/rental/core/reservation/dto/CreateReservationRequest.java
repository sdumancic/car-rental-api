package car.rental.core.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class CreateReservationRequest {
    private Long userId;
    private Long vehicleId;
    private Instant startDate;
    private Instant endDate;
}
