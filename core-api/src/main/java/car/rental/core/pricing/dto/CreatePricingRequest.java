package car.rental.core.pricing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePricingRequest {
    @NotNull
    private Long vehicleId;
    @NotNull
    private Long pricingCategoryId;
}
