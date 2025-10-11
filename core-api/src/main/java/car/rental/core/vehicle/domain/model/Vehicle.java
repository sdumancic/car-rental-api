package car.rental.core.vehicle.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class Vehicle {
    private Long id;
    private String make;
    private String model;
    private Integer year;
    private String vin;
    private String licensePlate;
    private VehicleType vehicleType;
    private VehicleStatus status;
    private Integer passengers;
    private Integer doors;
    private FuelType fuelType;
    private TransmissionType transmission;
    private Boolean active;
}
