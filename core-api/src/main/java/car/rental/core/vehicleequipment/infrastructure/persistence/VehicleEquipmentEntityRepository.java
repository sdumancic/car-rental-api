package car.rental.core.vehicleequipment.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VehicleEquipmentEntityRepository implements PanacheRepositoryBase<VehicleEquipmentEntity, VehicleEquipmentId> {

}
