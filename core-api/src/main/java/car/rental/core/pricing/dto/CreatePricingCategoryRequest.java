package car.rental.core.pricing.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreatePricingCategoryRequest {
    @NotNull
    private String name;
    private String description;
    @NotEmpty
    private List<PricingTierRequest> pricingTiers;
}
