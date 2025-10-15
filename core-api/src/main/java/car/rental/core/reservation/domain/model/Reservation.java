package car.rental.core.reservation.domain.model;

import car.rental.core.users.domain.model.User;
import car.rental.core.vehicle.domain.model.Vehicle;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Domain model for Reservation.
 * This is framework-agnostic and should not contain JPA annotations.
 */
@Getter
@Setter
@Builder
@ToString
public class Reservation {
    private Long id;
    private User user;
    private Vehicle vehicle;
    private Instant startDate;
    private Instant endDate;
    private BigDecimal price;
    private ReservationStatus status;
    private Instant dateCreated;
    private Instant dateModified;
}
