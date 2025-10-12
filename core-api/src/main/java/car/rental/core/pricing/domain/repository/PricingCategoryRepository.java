package car.rental.core.pricing.domain.repository;

import car.rental.core.common.domain.BaseRepository;
import car.rental.core.pricing.domain.model.PricingCategory;

import java.util.Optional;

public interface PricingCategoryRepository extends BaseRepository<PricingCategory> {
    Optional<PricingCategory> findByName(String name);

    PricingCategory update(PricingCategory pricingCategory);

    void softDeleteById(Long id);
}
