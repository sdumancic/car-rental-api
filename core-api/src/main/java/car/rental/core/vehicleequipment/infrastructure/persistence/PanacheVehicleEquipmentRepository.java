package car.rental.core.vehicleequipment.infrastructure.persistence;

import car.rental.core.vehicleequipment.domain.model.VehicleEquipment;
import car.rental.core.vehicleequipment.domain.repository.VehicleEquipmentRepository;
import car.rental.core.vehicleequipment.infrastructure.mapper.VehicleEquipmentMapper;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PanacheVehicleEquipmentRepository implements VehicleEquipmentRepository {

    private final VehicleEquipmentEntityRepository vehicleEquipmentEntityRepository;

    @Override
    public Optional<VehicleEquipment> findById(Long id) {
        // Not applicable for composite key
        return Optional.empty();
    }

    @Override
    public List<VehicleEquipment> findAll() {
        return vehicleEquipmentEntityRepository.listAll().stream().map(VehicleEquipmentMapper::toDomain).toList();
    }

    @Override
    public VehicleEquipment save(VehicleEquipment vehicleEquipment) {
        VehicleEquipmentEntity entity = VehicleEquipmentMapper.toEntity(vehicleEquipment);
        entity.setDateCreated(Instant.now());
        entity.setDateModified(Instant.now());
        entity.setActive(true);
        vehicleEquipmentEntityRepository.persist(entity);
        return VehicleEquipmentMapper.toDomain(entity);
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void deleteByVehicleIdAndEquipmentId(Long vehicleId, Long equipmentId) {
        VehicleEquipmentId id = new VehicleEquipmentId();
        id.setVehicleId(vehicleId);
        id.setEquipmentId(equipmentId);
        vehicleEquipmentEntityRepository.deleteById(id);
    }
}
