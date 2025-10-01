package car.rental.core.vehicleequipment.domain.model;

import car.rental.core.equipment.domain.model.Equipment;
import car.rental.core.vehicle.domain.model.Vehicle;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class VehicleEquipment {
    private Vehicle vehicle;
    private Equipment equipment;
}
