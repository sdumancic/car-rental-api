package car.rental.core.pricing.infrastructure.persistence;

import car.rental.core.pricing.infrastructure.PricingEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PricingEntityRepository implements PanacheRepository<PricingEntity> {

}
