package car.rental.core.equipment.infrastructure.mapper;

import car.rental.core.equipment.domain.model.Equipment;
import car.rental.core.equipment.dto.CreateEquipmentRequest;
import car.rental.core.equipment.infrastructure.persistence.EquipmentEntity;

public class EquipmentMapper {
    // --- API → Domain ---
    public static Equipment toDomain(CreateEquipmentRequest request) {
        if (request == null) {
            return null;
        }
        return Equipment.builder()
                .id(null)
                .name(request.getName())
                .description(request.getDescription())
                .active(request.getActive())
                .build();
    }

    // --- Domain → Entity ---
    public static EquipmentEntity toEntity(Equipment domain) {
        if (domain == null) {
            return null;
        }
        EquipmentEntity entity = new EquipmentEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setActive(domain.getActive());
        return entity;
    }

    // --- Entity → Domain ---
    public static Equipment toDomain(EquipmentEntity entity) {
        if (entity == null) {
            return null;
        }
        return Equipment.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .active(entity.getActive())
                .build();
    }
}
