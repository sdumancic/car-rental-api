package car.rental.core.pricing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PricingTierRequest {
    @NotNull
    @Min(1)
    private Integer minDays;
    private Integer maxDays; // Can be null for unlimited
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;
}
