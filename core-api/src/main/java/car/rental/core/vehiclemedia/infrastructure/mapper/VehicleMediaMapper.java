package car.rental.core.vehiclemedia.infrastructure.mapper;

import car.rental.core.vehicle.infrastructure.mapper.VehicleMapper;
import car.rental.core.vehicle.infrastructure.persistence.VehicleEntity;
import car.rental.core.vehiclemedia.domain.model.VehicleMedia;
import car.rental.core.vehiclemedia.dto.CreateVehicleMediaRequest;
import car.rental.core.vehiclemedia.infrastructure.persistence.VehicleMediaEntity;

public class VehicleMediaMapper {
    // --- API → Domain ---
    public static VehicleMedia toDomain(CreateVehicleMediaRequest request, VehicleEntity vehicleEntity) {
        if (request == null) {
            return null;
        }
        return VehicleMedia.builder()
                .id(null)
                .vehicleMediaType(request.getVehicleMediaType())
                .fileName(request.getFileName())
                .fileType(request.getFileType())
                .vehicle(VehicleMapper.toDomain(vehicleEntity))
                .build();
    }

    // --- Domain → Entity ---
    public static VehicleMediaEntity toEntity(VehicleMedia domain) {
        if (domain == null) {
            return null;
        }
        VehicleMediaEntity entity = new VehicleMediaEntity();
        entity.setId(domain.getId());
        entity.setVehicleMediaType(domain.getVehicleMediaType());
        entity.setFileName(domain.getFileName());
        entity.setFileType(domain.getFileType());
        entity.setVehicleEntity(VehicleMapper.toEntity(domain.getVehicle()));
        return entity;
    }

    // --- Entity → Domain ---
    public static VehicleMedia toDomain(VehicleMediaEntity entity) {
        if (entity == null) {
            return null;
        }
        return VehicleMedia.builder()
                .id(entity.getId())
                .vehicleMediaType(entity.getVehicleMediaType())
                .fileName(entity.getFileName())
                .fileType(entity.getFileType())
                .url(entity.getUrl())
                .vehicle(VehicleMapper.toDomain(entity.getVehicleEntity()))
                .build();
    }
}
