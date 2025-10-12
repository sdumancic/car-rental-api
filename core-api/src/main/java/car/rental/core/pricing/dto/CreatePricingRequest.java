package car.rental.core.pricing.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreatePricingRequest {
    @NotNull
    private Long vehicleId;
    @NotNull
    private String categoryName;
    private String categoryDescription;
    @NotEmpty
    private List<PricingTierRequest> pricingTiers;
}
