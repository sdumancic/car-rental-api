package car.rental.core.vehicleequipment.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class VehicleEquipmentId {

    @Column(name = "vehicle_id")
    private Long vehicleId;
    @Column(name = "equipment_id")
    private Long equipmentId;
}
