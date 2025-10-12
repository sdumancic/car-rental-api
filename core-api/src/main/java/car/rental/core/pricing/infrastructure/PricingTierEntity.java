package car.rental.core.pricing.infrastructure;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "pricing_tier", indexes = {
        @Index(name = "idx_pricing_tier_pricing_category_id", columnList = "pricing_category_id")
})
@NoArgsConstructor
public class PricingTierEntity extends car.rental.core.common.domain.BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pricing_category_id", nullable = false)
    private PricingCategoryEntity pricingCategory;

    @Column(name = "min_days", nullable = false)
    @NotNull
    @Min(1)
    private Integer minDays;

    @Column(name = "max_days")
    private Integer maxDays;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;
}
