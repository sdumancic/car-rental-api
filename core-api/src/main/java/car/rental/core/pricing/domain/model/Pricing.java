package car.rental.core.pricing.domain.model;

import car.rental.core.vehicle.domain.model.Vehicle;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

/**
 * Domain model for Pricing.
 * This is framework-agnostic and should not contain JPA annotations.
 */
@Getter
@Setter
@Builder
@ToString
public class Pricing {
    private Long id;
    private Vehicle vehicle;
    private PricingCategory pricingCategory;
    private Boolean active;
    private Instant dateCreated;
    private Instant dateModified;
}
