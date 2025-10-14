package car.rental.core.equipment.infrastructure.persistence;

import car.rental.core.common.exception.ResourceNotFoundException;
import car.rental.core.equipment.domain.model.Equipment;
import car.rental.core.equipment.domain.repository.EquipmentRepository;
import car.rental.core.equipment.infrastructure.mapper.EquipmentMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
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
    public Optional<Equipment> findById(Long id) {
        return equipmentEntityRepository.findByIdOptional(id).map(EquipmentMapper::toDomain);
    }

    @Override
    public List<Equipment> findAll() {
        return equipmentEntityRepository.listAll().stream().map(EquipmentMapper::toDomain).toList();
    }

    @Transactional
    @Override
    public Equipment save(Equipment vehicle) {
        EquipmentEntity entity = EquipmentMapper.toEntity(vehicle);
        entity.setDateCreated(Instant.now());
        entity.setDateModified(Instant.now());
        equipmentEntityRepository.persist(entity);
        return EquipmentMapper.toDomain(entity);
    }

    @Transactional
    @Override
    public Equipment update(Equipment equipment) {
        EquipmentEntity entity = equipmentEntityRepository.findById(equipment.getId());
        if (entity == null) {
            throw new ResourceNotFoundException("Equipment not found for id: " + equipment.getId());
        }
        EquipmentMapper.updateEntity(entity, equipment);
        return EquipmentMapper.toDomain(entity);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        EquipmentEntity entity = equipmentEntityRepository.findById(id);
        if (entity != null) {
            equipmentEntityRepository.delete(entity);
        }
    }

    @Transactional
    @Override
    public void softDeleteById(Long id) {
        EquipmentEntity entity = equipmentEntityRepository.findById(id);
        if (entity != null) {
            entity.setActive(false);
            entity.setDateModified(Instant.now());
            equipmentEntityRepository.persist(entity);
        }
    }
}
