package car.rental.core.pricing.infrastructure;

import car.rental.core.common.domain.BaseEntity;
import car.rental.core.vehicle.infrastructure.persistence.VehicleEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "pricing",
        indexes = {
                @Index(name = "idx_pricing_vehicle_id", columnList = "vehicle_id"),
                @Index(name = "idx_pricing_pricing_category_id", columnList = "pricing_category_id")
        }
)
@NoArgsConstructor
public class PricingEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private VehicleEntity vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pricing_category_id", nullable = false)
    private PricingCategoryEntity pricingCategory;

    @Column(name = "active", nullable = false)
    private Boolean active;
}
