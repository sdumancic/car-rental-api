package car.rental.core.equipment.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EquipmentEntityRepository implements PanacheRepository<EquipmentEntity> {

}
