package car.rental.core.vehicle.dto;

import car.rental.core.vehicle.domain.model.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleAvailabilityDto {
    private Vehicle vehicle;
    private boolean available;
}

