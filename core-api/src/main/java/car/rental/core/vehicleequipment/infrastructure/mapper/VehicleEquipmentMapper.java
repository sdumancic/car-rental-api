package car.rental.core.vehicleequipment.infrastructure.mapper;

import car.rental.core.equipment.infrastructure.mapper.EquipmentMapper;
import car.rental.core.vehicle.infrastructure.mapper.VehicleMapper;
import car.rental.core.vehicleequipment.domain.model.VehicleEquipment;
import car.rental.core.vehicleequipment.infrastructure.persistence.VehicleEquipmentEntity;
import car.rental.core.vehicleequipment.infrastructure.persistence.VehicleEquipmentId;

public class VehicleEquipmentMapper {
    // --- API → Domain ---
    // --- Domain → Entity ---
    public static VehicleEquipmentEntity toEntity(VehicleEquipment domain) {
        if (domain == null) {
            return null;
        }
        VehicleEquipmentId id = new VehicleEquipmentId();
        id.setVehicleId(domain.getVehicle().getId());
        id.setEquipmentId(domain.getEquipment().getId());

        VehicleEquipmentEntity entity = new VehicleEquipmentEntity();
        entity.setId(id);
        entity.setVehicle(VehicleMapper.toEntity(domain.getVehicle()));
        entity.setEquipment(EquipmentMapper.toEntity(domain.getEquipment()));
        return entity;
    }

    // --- Entity → Domain ---
    public static VehicleEquipment toDomain(VehicleEquipmentEntity entity) {
        if (entity == null) {
            return null;
        }
        VehicleEquipmentId id = new VehicleEquipmentId();
        id.setVehicleId(entity.getVehicle().getId());
        id.setEquipmentId(entity.getEquipment().getId());

        return VehicleEquipment.builder()
                .equipment(EquipmentMapper.toDomain(entity.getEquipment()))
                .vehicle(VehicleMapper.toDomain(entity.getVehicle()))
                .build();
    }
}
