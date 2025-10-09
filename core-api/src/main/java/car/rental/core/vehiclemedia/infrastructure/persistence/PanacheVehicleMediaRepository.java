package car.rental.core.vehiclemedia.infrastructure.persistence;

import car.rental.core.vehiclemedia.domain.model.VehicleMedia;
import car.rental.core.vehiclemedia.domain.repository.VehicleMediaRepository;
import car.rental.core.vehiclemedia.infrastructure.mapper.VehicleMediaMapper;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PanacheVehicleMediaRepository implements VehicleMediaRepository {

    private final VehicleMediaEntityRepository vehicleMEdiaEntityRepository;

    @Override
    public Optional<VehicleMedia> findById(Long id) {
        return vehicleMEdiaEntityRepository.findByIdOptional(id).map(VehicleMediaMapper::toDomain);
    }

    @Override
    public List<VehicleMedia> findAll() {
        return vehicleMEdiaEntityRepository.findAll().stream().map(VehicleMediaMapper::toDomain).toList();
    }

    @Override
    public List<VehicleMedia> findAllForVehicle(Long vehicleId) {
        return vehicleMEdiaEntityRepository.find("vehicleEntity.id", vehicleId)
                .stream().map(VehicleMediaMapper::toDomain).toList();
    }

    @Override
    public VehicleMedia save(VehicleMedia vehicleMedia) {
        VehicleMediaEntity entity = VehicleMediaMapper.toEntity(vehicleMedia);
        entity.setDateCreated(Instant.now());
        entity.setDateModified(Instant.now());
        vehicleMEdiaEntityRepository.persist(entity);
        return VehicleMediaMapper.toDomain(entity);
    }

    public VehicleMedia updateUrl(VehicleMedia vehicleMedia) {
        VehicleMediaEntity vehicleMediaEntity = vehicleMEdiaEntityRepository.findById(vehicleMedia.getId());
        if (vehicleMediaEntity == null) {
            throw new IllegalArgumentException("VehicleMedia with ID " + vehicleMedia.getId() + " not found.");
        }
        vehicleMediaEntity.setUrl(vehicleMedia.getUrl());
        vehicleMediaEntity.setDateModified(Instant.now());
        vehicleMEdiaEntityRepository.persist(vehicleMediaEntity);
        return VehicleMediaMapper.toDomain(vehicleMediaEntity);
    }


    @Override
    public void deleteById(Long id) {

    }
}
