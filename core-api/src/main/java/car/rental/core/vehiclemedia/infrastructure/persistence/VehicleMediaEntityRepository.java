package car.rental.core.vehiclemedia.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VehicleMediaEntityRepository implements PanacheRepository<VehicleMediaEntity> {

}
