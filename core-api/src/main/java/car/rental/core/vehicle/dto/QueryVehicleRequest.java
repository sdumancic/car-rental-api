package car.rental.core.vehicle.dto;

import car.rental.core.vehicle.domain.model.FuelType;
import car.rental.core.vehicle.domain.model.TransmissionType;
import car.rental.core.vehicle.domain.model.VehicleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryVehicleRequest {
    private String make;
    private String model;
    private Integer year;
    private VehicleType vehicleType;
    private Integer passengers;
    private Integer doors;
    private FuelType fuelType;
    private TransmissionType transmission;
    private Integer page = 0;
    private Integer size = 10;
    private String sort;
}
