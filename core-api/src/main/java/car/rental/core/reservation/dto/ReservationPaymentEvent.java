package car.rental.core.reservation.dto;

import car.rental.core.reservation.domain.model.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPaymentEvent {
    private Long reservationId;
    private Long userId;
    private Long vehicleId;
    private Instant startDate;
    private Instant endDate;
    private BigDecimal price;
    private ReservationStatus status;
    private Instant dateCreated;
    private Instant dateModified;
}

