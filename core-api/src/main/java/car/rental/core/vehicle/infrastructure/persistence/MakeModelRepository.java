package car.rental.core.vehicle.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MakeModelRepository implements PanacheRepository<MakeModelEntity> {
    // Custom queries can be added here if needed
}

