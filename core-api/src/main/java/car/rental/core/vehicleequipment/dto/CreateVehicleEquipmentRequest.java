package car.rental.core.vehicleequipment.dto;

import car.rental.core.equipment.domain.model.Equipment;
import car.rental.core.vehicle.domain.model.Vehicle;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVehicleEquipmentRequest {

    private Vehicle vehicle;
    private Equipment equipment;
}

