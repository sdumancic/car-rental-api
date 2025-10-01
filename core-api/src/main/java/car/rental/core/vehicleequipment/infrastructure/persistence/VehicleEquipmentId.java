package car.rental.core.vehicleequipment.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class VehicleEquipmentId {

    @Column(name = "vehicle_id")
    private Long vehicleId;
    @Column(name = "equipment_id")
    private Long equipmentId;
}
