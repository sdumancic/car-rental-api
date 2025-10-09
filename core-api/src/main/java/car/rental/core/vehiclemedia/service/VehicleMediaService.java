package car.rental.core.vehiclemedia.service;

import car.rental.core.vehicle.domain.model.Vehicle;
import car.rental.core.vehicle.infrastructure.mapper.VehicleMapper;
import car.rental.core.vehicle.infrastructure.persistence.PanacheVehicleRepository;
import car.rental.core.vehicle.infrastructure.persistence.VehicleEntity;
import car.rental.core.vehiclemedia.domain.model.VehicleMedia;
import car.rental.core.vehiclemedia.dto.CreateVehicleMediaRequest;
import car.rental.core.vehiclemedia.infrastructure.mapper.VehicleMediaMapper;
import car.rental.core.vehiclemedia.infrastructure.persistence.PanacheVehicleMediaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class VehicleMediaService {

    private final PanacheVehicleMediaRepository panacheVehicleMediaRepository;
    private final PanacheVehicleRepository panacheVehicleRepository;

    @Transactional
    public VehicleMedia createVehicleMedia(CreateVehicleMediaRequest request) {
        Vehicle vehicle = panacheVehicleRepository.findById(request.getVehicleId()).orElse(null);
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle with ID " + request.getVehicleId() + " not found.");
        }
        VehicleEntity vehicleEntity = VehicleMapper.toEntity(vehicle);
        VehicleMedia vehicleMedia = VehicleMediaMapper.toDomain(request, vehicleEntity);
        return panacheVehicleMediaRepository.save(vehicleMedia);
    }

    public VehicleMedia findById(Long id) {
        return panacheVehicleMediaRepository.findById(id).orElse(null);
    }

    public List<VehicleMedia> findAllVehicleMediaForVehicle(Long vehicleId) {
        Vehicle vehicle = panacheVehicleRepository.findById(vehicleId).orElse(null);
        if (vehicle == null) {
            return List.of();
        }
        return panacheVehicleMediaRepository.findAllForVehicle(vehicleId).stream()
                .filter(vm -> vm.getVehicle().getId().equals(vehicleId))
                .toList();
    }

    @Transactional
    public VehicleMedia setMediaBlobUrl(VehicleMedia vehicleMedia, String blobUrl) {
        vehicleMedia.setUrl(blobUrl);
        return panacheVehicleMediaRepository.updateUrl(vehicleMedia);
    }
}
