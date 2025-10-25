package car.rental.langchain.infrastructure.persistence;

import car.rental.langchain.domain.model.ReservationStatus;
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

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "vehicle_id")
    private Long vehicleId;

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
