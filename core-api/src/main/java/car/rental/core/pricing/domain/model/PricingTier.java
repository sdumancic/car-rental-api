package car.rental.core.pricing.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PricingTier {
    private Integer minDays;
    private Integer maxDays;
    private BigDecimal price;
}
