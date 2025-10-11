package car.rental.core.vehiclemedia.service;

import car.rental.core.azure.service.AzureBlobService;
import car.rental.core.vehicle.domain.model.Vehicle;
import car.rental.core.vehicle.infrastructure.mapper.VehicleMapper;
import car.rental.core.vehicle.infrastructure.persistence.PanacheVehicleRepository;
import car.rental.core.vehicle.infrastructure.persistence.VehicleEntity;
import car.rental.core.vehiclemedia.domain.model.VehicleMedia;
import car.rental.core.vehiclemedia.dto.CreateVehicleMediaRequest;
import car.rental.core.vehiclemedia.infrastructure.mapper.VehicleMediaMapper;
import car.rental.core.vehiclemedia.infrastructure.persistence.PanacheVehicleMediaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class VehicleMediaService {

    private final PanacheVehicleMediaRepository panacheVehicleMediaRepository;
    private final PanacheVehicleRepository panacheVehicleRepository;

    @Inject
    AzureBlobService azureBlobService;

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

    public byte[] downloadMedia(Long mediaId) {
        VehicleMedia vehicleMedia = findById(mediaId);
        if (vehicleMedia == null) {
            throw new IllegalArgumentException("Media not found");
        }
        if (vehicleMedia.getUrl() == null) {
            throw new IllegalArgumentException("Media not uploaded yet");
        }
        // Extract blob name from URL robustly
        String blobUrl = vehicleMedia.getUrl();
        String marker = "/vehicles/";
        int idx = blobUrl.indexOf(marker);
        if (idx == -1) {
            throw new IllegalArgumentException("Invalid blob URL");
        }
        String encodedBlobName = blobUrl.substring(idx + marker.length());
        String blobName = URLDecoder.decode(encodedBlobName, StandardCharsets.UTF_8);
        // Remove leading vehicleId/ if present
        String vehicleIdPrefix = vehicleMedia.getVehicle().getId() + "/";
        if (blobName.startsWith(vehicleIdPrefix)) {
            blobName = blobName.substring(vehicleIdPrefix.length());
        }
        return azureBlobService.downloadBlob(blobName, vehicleMedia.getVehicle().getId());
    }
}
