package car.rental.core.vehicle.dto;

import car.rental.core.vehicle.domain.model.FuelType;
import car.rental.core.vehicle.domain.model.TransmissionType;
import car.rental.core.vehicle.domain.model.VehicleStatus;
import car.rental.core.vehicle.domain.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVehicleRequest {
    @NotBlank
    private String make;
    @NotBlank
    private String model;
    @NotNull
    private Integer year;
    private String vin;
    private String licensePlate;
    private VehicleType vehicleType;
    private VehicleStatus status;
    private Integer passengers;
    private Integer doors;
    private FuelType fuelType;
    private TransmissionType transmission;
}

