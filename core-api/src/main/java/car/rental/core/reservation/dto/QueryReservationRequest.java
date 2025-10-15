package car.rental.core.reservation.dto;

import car.rental.core.reservation.domain.model.ReservationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class QueryReservationRequest {
    private Integer page;
    private Integer size;
    private Long userId;
    private Long vehicleId;
    private Instant startDate;
    private Instant endDate;
    private ReservationStatus status;
    private String sort;
}
