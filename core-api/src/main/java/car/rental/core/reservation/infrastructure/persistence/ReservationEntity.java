package car.rental.core.reservation.infrastructure.persistence;

import car.rental.core.common.domain.BaseEntity;
import car.rental.core.reservation.domain.model.ReservationStatus;
import car.rental.core.users.infrastructure.persistence.UserEntity;
import car.rental.core.vehicle.infrastructure.persistence.VehicleEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "reservations", indexes = {
        @Index(name = "idx_reservations_user_id", columnList = "user_id,start_date"),
        @Index(name = "idx_reservations_vehicle_id", columnList = "vehicle_id,start_date")
})
@Getter
@Setter
@NoArgsConstructor
public class ReservationEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private VehicleEntity vehicle;

    @Column(name = "start_date", columnDefinition = "datetime2")
    private Instant startDate;

    @Column(name = "end_date", columnDefinition = "datetime2")
    private Instant endDate;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(name = "active", nullable = false)
    private Boolean active;
}
