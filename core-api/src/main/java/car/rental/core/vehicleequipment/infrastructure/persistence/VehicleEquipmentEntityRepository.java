package car.rental.core.vehicleequipment.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VehicleEquipmentEntityRepository implements PanacheRepository<VehicleEquipmentEntity> {

}
