package car.rental.core.pricing.infrastructure;

import car.rental.core.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "pricing_category"
)
@NoArgsConstructor
public class PricingCategoryEntity extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "pricingCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PricingTierEntity> pricingTiers;

    @Column(name = "active", nullable = false)
    private Boolean active;
}
