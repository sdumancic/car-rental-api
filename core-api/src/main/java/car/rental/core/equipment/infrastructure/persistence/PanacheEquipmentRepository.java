package car.rental.core.equipment.infrastructure.persistence;

import car.rental.core.equipment.domain.model.Equipment;
import car.rental.core.equipment.domain.repository.EquipmentRepository;
import car.rental.core.equipment.infrastructure.mapper.EquipmentMapper;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PanacheEquipmentRepository implements EquipmentRepository {

    private final EquipmentEntityRepository equipmentEntityRepository;

    @Override
    public Optional findById(Long id) {
        return equipmentEntityRepository.findByIdOptional(id).map(EquipmentMapper::toDomain);
    }

    @Override
    public List findAll() {
        return equipmentEntityRepository.listAll().stream().map(EquipmentMapper::toDomain).toList();
    }

    @Override
    public Equipment save(Equipment vehicle) {
        EquipmentEntity entity = EquipmentMapper.toEntity(vehicle);
        entity.setDateCreated(Instant.now());
        entity.setDateModified(Instant.now());
        equipmentEntityRepository.persist(entity);
        return EquipmentMapper.toDomain(entity);
    }

    @Override
    public void deleteById(Long id) {

    }
}
