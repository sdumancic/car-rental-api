package car.rental.core.vehicleequipment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVehicleEquipmentRequest {

    private Long vehicleId;
    private Long equipmentId;
}

