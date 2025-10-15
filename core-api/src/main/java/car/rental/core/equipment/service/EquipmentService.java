package car.rental.core.equipment.service;

import car.rental.core.common.exception.ResourceNotFoundException;
import car.rental.core.equipment.domain.model.Equipment;
import car.rental.core.equipment.domain.repository.EquipmentRepository;
import car.rental.core.equipment.dto.CreateEquipmentRequest;
import car.rental.core.equipment.infrastructure.mapper.EquipmentMapper;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public Equipment createEquipment(CreateEquipmentRequest request) {

        Equipment equipment = EquipmentMapper.toDomain(request);
        return equipmentRepository.save(equipment);
    }

    public Equipment findEquipmentById(Long id) {
        return equipmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));
    }

    public Equipment updateEquipment(Long id, CreateEquipmentRequest request) {
        Equipment equipment = EquipmentMapper.toDomain(request);
        equipment.setId(id);
        return equipmentRepository.update(equipment);
    }

    public void softDeleteEquipment(Long id) {
        equipmentRepository.softDeleteById(id);
    }

    public List<Equipment> findAll() {
        return equipmentRepository.findAll();
    }
}
