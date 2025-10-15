package car.rental.core.vehicle.infrastructure.mapper;

import car.rental.core.vehicle.domain.model.Vehicle;
import car.rental.core.vehicle.dto.CreateVehicleRequest;
import car.rental.core.vehicle.infrastructure.persistence.VehicleEntity;

public class VehicleMapper {
    // --- API → Domain ---
    public static Vehicle toDomain(CreateVehicleRequest request) {
        if (request == null) {
            return null;
        }
        return Vehicle.builder()
                .id(null)
                .make(request.getMake())
                .model(request.getModel())
                .year(request.getYear())
                .vin(request.getVin())
                .licensePlate(request.getLicensePlate())
                .vehicleType(request.getVehicleType())
                .status(request.getStatus())
                .passengers(request.getPassengers())
                .doors(request.getDoors())
                .fuelType(request.getFuelType())
                .transmission(request.getTransmission())
                .active(true)
                .build();
    }

    // --- Domain → Entity ---
    public static VehicleEntity toEntity(Vehicle domain) {
        if (domain == null) {
            return null;
        }
        VehicleEntity entity = new VehicleEntity();
        entity.setId(domain.getId());
        entity.setMake(domain.getMake());
        entity.setModel(domain.getModel());
        entity.setYear(domain.getYear());
        entity.setVin(domain.getVin());
        entity.setLicensePlate(domain.getLicensePlate());
        entity.setVehicleType(domain.getVehicleType());
        entity.setStatus(domain.getStatus());
        entity.setPassengers(domain.getPassengers());
        entity.setDoors(domain.getDoors());
        entity.setFuelType(domain.getFuelType());
        entity.setTransmission(domain.getTransmission());
        entity.setActive(domain.getActive());
        return entity;
    }

    // --- Entity → Domain ---
    public static Vehicle toDomain(VehicleEntity entity) {
        if (entity == null) {
            return null;
        }
        return Vehicle.builder()
                .id(entity.getId())
                .make(entity.getMake())
                .model(entity.getModel())
                .year(entity.getYear())
                .vin(entity.getVin())
                .licensePlate(entity.getLicensePlate())
                .vehicleType(entity.getVehicleType())
                .status(entity.getStatus())
                .passengers(entity.getPassengers())
                .doors(entity.getDoors())
                .fuelType(entity.getFuelType())
                .transmission(entity.getTransmission())
                .build();
    }

    public static void updateEntity(VehicleEntity entity, Vehicle domain) {
        if (entity == null || domain == null) {
            return;
        }
        entity.setMake(domain.getMake());
        entity.setModel(domain.getModel());
        entity.setYear(domain.getYear());
        entity.setVin(domain.getVin());
        entity.setLicensePlate(domain.getLicensePlate());
        entity.setVehicleType(domain.getVehicleType());
        entity.setStatus(domain.getStatus());
        entity.setPassengers(domain.getPassengers());
        entity.setDoors(domain.getDoors());
        entity.setFuelType(domain.getFuelType());
        entity.setTransmission(domain.getTransmission());
    }
}
