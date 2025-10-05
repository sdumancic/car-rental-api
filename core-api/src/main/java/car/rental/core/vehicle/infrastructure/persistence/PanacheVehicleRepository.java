package car.rental.core.vehicle.infrastructure.persistence;

import car.rental.core.vehicle.domain.model.Vehicle;
import car.rental.core.vehicle.domain.repository.VehicleRepository;
import car.rental.core.vehicle.infrastructure.mapper.VehicleMapper;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PanacheVehicleRepository implements VehicleRepository {

    private final VehicleEntityRepository vehicleEntityRepository;

    @Override
    public Optional<Vehicle> findById(Long id) {
        return vehicleEntityRepository.findByIdOptional(id).map(VehicleMapper::toDomain);
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicleEntityRepository.listAll().stream().map(VehicleMapper::toDomain).toList();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        VehicleEntity entity = VehicleMapper.toEntity(vehicle);
        entity.setDateCreated(Instant.now());
        entity.setDateModified(Instant.now());
        vehicleEntityRepository.persist(entity);
        return VehicleMapper.toDomain(entity);
    }

    @Override
    public void deleteById(Long id) {

    }
}
