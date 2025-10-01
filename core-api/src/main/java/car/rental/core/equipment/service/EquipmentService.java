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
}
