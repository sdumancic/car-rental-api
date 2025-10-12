package car.rental.core.pricing.infrastructure.persistence;

import car.rental.core.pricing.infrastructure.PricingCategoryEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PricingCategoryEntityRepository implements PanacheRepository<PricingCategoryEntity> {

}
