package car.rental.core.pricing.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

/**
 * Domain model for PricingCategory.
 * This is framework-agnostic and should not contain JPA annotations.
 */
@Getter
@Setter
@Builder
@ToString
public class PricingCategory {
    private Long id;
    private String name;
    private String description;
    private List<PricingTier> pricingTiers;
    private Boolean active;
    private Instant dateCreated;
    private Instant dateModified;
}
