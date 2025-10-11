package car.rental.core.vehicle.infrastructure.persistence;

import car.rental.core.common.domain.BaseEntity;
import car.rental.core.vehicle.domain.model.FuelType;
import car.rental.core.vehicle.domain.model.TransmissionType;
import car.rental.core.vehicle.domain.model.VehicleStatus;
import car.rental.core.vehicle.domain.model.VehicleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "vehicles")
@ToString
public class VehicleEntity extends BaseEntity {

    private String make;
    private String model;
    private Integer year;
    private String vin;
    @Column(name = "license_plate")
    private String licensePlate;
    @Column(name = "vehicle_type")
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
    @Column(name = "vehicle_status")
    @Enumerated(EnumType.STRING)
    private VehicleStatus status;
    private Integer passengers;
    private Integer doors;
    @Column(name = "fuel_type")
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;
    @Column(name = "transmission_type")
    @Enumerated(EnumType.STRING)
    private TransmissionType transmission;
    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
