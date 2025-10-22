package car.rental.core.vehicle.service;

import car.rental.core.vehicle.domain.model.FuelType;
import car.rental.core.vehicle.domain.model.TransmissionType;
import car.rental.core.vehicle.domain.model.VehicleStatus;
import car.rental.core.vehicle.domain.model.VehicleType;
import car.rental.core.vehicle.infrastructure.persistence.MakeModelEntity;
import car.rental.core.vehicle.infrastructure.persistence.MakeModelRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class VehicleMetadataService {
    @Inject
    MakeModelRepository makeModelRepository;

    public List<String> getAllMakes() {
        return makeModelRepository.findAll().stream()
                .map(MakeModelEntity::getMake)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getModelsByMake(String make) {
        return makeModelRepository.find("make", make).stream()
                .map(MakeModelEntity::getModel)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getAllVehicleTypes() {
        return List.of(VehicleType.values()).stream().map(Enum::name).collect(Collectors.toList());
    }

    public List<String> getAllTransmissionTypes() {
        return List.of(TransmissionType.values()).stream().map(Enum::name).collect(Collectors.toList());
    }

    public List<String> getAllFuelTypes() {
        return List.of(FuelType.values()).stream().map(Enum::name).collect(Collectors.toList());
    }

    public List<String> getAllVehicleStatuses() {
        return List.of(VehicleStatus.values()).stream().map(Enum::name).collect(Collectors.toList());
    }
}

