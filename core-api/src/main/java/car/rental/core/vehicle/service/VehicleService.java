package car.rental.core.vehicle.service;

import car.rental.core.vehicle.domain.model.Vehicle;
import car.rental.core.vehicle.dto.CreateVehicleRequest;
import car.rental.core.vehicle.infrastructure.mapper.VehicleMapper;
import car.rental.core.vehicle.infrastructure.persistence.PanacheVehicleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class VehicleService {

    private final PanacheVehicleRepository panacheVehicleRepository;

    @Transactional
    public Vehicle createVehicle(CreateVehicleRequest request) {
        Vehicle vehicle = VehicleMapper.toDomain(request);
        return panacheVehicleRepository.save(vehicle);
    }
}
