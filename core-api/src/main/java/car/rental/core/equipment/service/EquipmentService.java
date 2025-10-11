package car.rental.core.equipment.service;

import car.rental.core.equipment.domain.model.Equipment;
import car.rental.core.equipment.dto.CreateEquipmentRequest;
import car.rental.core.equipment.infrastructure.mapper.EquipmentMapper;
import car.rental.core.equipment.infrastructure.persistence.PanacheEquipmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class EquipmentService {

    private final PanacheEquipmentRepository panacheEquipmentRepository;

    @Transactional
    public Equipment createEquipment(CreateEquipmentRequest request) {

        Equipment equipment = EquipmentMapper.toDomain(request);
        return panacheEquipmentRepository.save(equipment);
    }

    public Equipment findEquipmentById(Long id) {
        return panacheEquipmentRepository.findById(id).orElse(null);
    }

    @Transactional
    public Equipment updateEquipment(Long id, CreateEquipmentRequest request) {
        Equipment existing = findEquipmentById(id);
        if (existing == null) {
            throw new RuntimeException("Equipment not found");
        }
        Equipment updated = Equipment.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .active(existing.getActive())
                .build();
        return panacheEquipmentRepository.update(updated);
    }

    @Transactional
    public void softDeleteEquipment(Long id) {
        panacheEquipmentRepository.softDeleteById(id);
    }
}
