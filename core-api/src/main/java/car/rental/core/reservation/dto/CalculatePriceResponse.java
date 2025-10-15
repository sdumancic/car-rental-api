package car.rental.core.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
public class CalculatePriceResponse {
    private Long vehicleId;
    private Instant startDate;
    private Instant endDate;
    private Integer days;
    private BigDecimal price;
}
