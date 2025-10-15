package car.rental.core.vehicle.infrastructure.persistence;

import car.rental.core.common.exception.ResourceNotFoundException;
import car.rental.core.common.util.SortUtils;
import car.rental.core.vehicle.domain.model.Vehicle;
import car.rental.core.vehicle.domain.repository.VehicleRepository;
import car.rental.core.vehicle.dto.QueryVehicleRequest;
import car.rental.core.vehicle.infrastructure.mapper.VehicleMapper;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PanacheVehicleRepository implements VehicleRepository {

    private final VehicleEntityRepository vehicleEntityRepository;

    @Override
    public Optional<Vehicle> findById(Long id) {
        return vehicleEntityRepository.findByIdOptional(id).map(VehicleMapper::toDomain);
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicleEntityRepository.listAll().stream().map(VehicleMapper::toDomain).toList();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        VehicleEntity entity = VehicleMapper.toEntity(vehicle);
        entity.setDateCreated(Instant.now());
        entity.setDateModified(Instant.now());
        vehicleEntityRepository.persist(entity);
        return VehicleMapper.toDomain(entity);
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public List<Vehicle> findByQuery(QueryVehicleRequest query) {
        StringBuilder queryBuilder = new StringBuilder("1=1");
        List<Object> params = new ArrayList<>();

        if (query.getMake() != null && !query.getMake().trim().isEmpty()) {
            queryBuilder.append(" and lower(make) like lower(?").append(params.size() + 1).append(")");
            params.add("%" + query.getMake().trim() + "%");
        }

        if (query.getModel() != null && !query.getModel().trim().isEmpty()) {
            queryBuilder.append(" and lower(model) like lower(?").append(params.size() + 1).append(")");
            params.add("%" + query.getModel().trim() + "%");
        }

        if (query.getYear() != null) {
            queryBuilder.append(" and year = ?").append(params.size() + 1);
            params.add(query.getYear());
        }

        if (query.getVehicleType() != null) {
            queryBuilder.append(" and vehicleType = ?").append(params.size() + 1);
            params.add(query.getVehicleType());
        }

        if (query.getPassengers() != null) {
            queryBuilder.append(" and passengers = ?").append(params.size() + 1);
            params.add(query.getPassengers());
        }

        if (query.getDoors() != null) {
            queryBuilder.append(" and doors = ?").append(params.size() + 1);
            params.add(query.getDoors());
        }

        if (query.getFuelType() != null) {
            queryBuilder.append(" and fuelType = ?").append(params.size() + 1);
            params.add(query.getFuelType());
        }

        if (query.getTransmission() != null) {
            queryBuilder.append(" and transmission = ?").append(params.size() + 1);
            params.add(query.getTransmission());
        }

        var panacheQuery = vehicleEntityRepository.find(queryBuilder.toString(), params.toArray());

        // Apply sorting if specified
        if (query.getSort() != null && !query.getSort().trim().isEmpty()) {
            Sort sort = SortUtils.createSort(query.getSort());
            panacheQuery = vehicleEntityRepository.find(queryBuilder.toString(), sort, params.toArray());
        }

        return panacheQuery.page(query.getPage(), query.getSize())
                .list()
                .stream()
                .map(VehicleMapper::toDomain)
                .toList();
    }

    @Override
    public long countByQuery(QueryVehicleRequest query) {
        StringBuilder queryBuilder = new StringBuilder("1=1");
        List<Object> params = new ArrayList<>();

        if (query.getMake() != null && !query.getMake().trim().isEmpty()) {
            queryBuilder.append(" and lower(make) like lower(?").append(params.size() + 1).append(")");
            params.add("%" + query.getMake().trim() + "%");
        }

        if (query.getModel() != null && !query.getModel().trim().isEmpty()) {
            queryBuilder.append(" and lower(model) like lower(?").append(params.size() + 1).append(")");
            params.add("%" + query.getModel().trim() + "%");
        }

        if (query.getYear() != null) {
            queryBuilder.append(" and year = ?").append(params.size() + 1);
            params.add(query.getYear());
        }

        if (query.getVehicleType() != null) {
            queryBuilder.append(" and vehicleType = ?").append(params.size() + 1);
            params.add(query.getVehicleType());
        }

        if (query.getPassengers() != null) {
            queryBuilder.append(" and passengers = ?").append(params.size() + 1);
            params.add(query.getPassengers());
        }

        if (query.getDoors() != null) {
            queryBuilder.append(" and doors = ?").append(params.size() + 1);
            params.add(query.getDoors());
        }

        if (query.getFuelType() != null) {
            queryBuilder.append(" and fuelType = ?").append(params.size() + 1);
            params.add(query.getFuelType());
        }

        if (query.getTransmission() != null) {
            queryBuilder.append(" and transmission = ?").append(params.size() + 1);
            params.add(query.getTransmission());
        }

        return vehicleEntityRepository.count(queryBuilder.toString(), params.toArray());
    }

    @Override
    @Transactional
    public Vehicle update(Vehicle vehicle) {
        VehicleEntity vehicleEntity = vehicleEntityRepository.findByIdOptional(vehicle.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with id '" + vehicle.getId() + "' does not exist"));
        VehicleMapper.updateEntity(vehicleEntity, vehicle);
        vehicleEntity.setDateModified(Instant.now());
        return VehicleMapper.toDomain(vehicleEntity);
    }

    @Override
    public void softDeleteById(Long id) {
        VehicleEntity entity = vehicleEntityRepository.findById(id);
        if (entity != null) {
            entity.setActive(false);
            entity.setDateModified(Instant.now());
            vehicleEntityRepository.persist(entity);
        }
    }
}
