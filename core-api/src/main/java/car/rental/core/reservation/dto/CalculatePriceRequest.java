package car.rental.core.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CalculatePriceRequest {
    private Long vehicleId;
    private LocalDate startDate;
    private LocalDate endDate;
}
