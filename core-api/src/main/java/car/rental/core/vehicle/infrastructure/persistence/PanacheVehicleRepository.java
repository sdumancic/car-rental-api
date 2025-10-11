package car.rental.core.vehicle.infrastructure.persistence;

import car.rental.core.vehicle.domain.model.Vehicle;
import car.rental.core.vehicle.domain.repository.VehicleRepository;
import car.rental.core.vehicle.dto.QueryVehicleRequest;
import car.rental.core.vehicle.infrastructure.mapper.VehicleMapper;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
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
            Sort sort = createSort(query.getSort());
            panacheQuery = vehicleEntityRepository.find(queryBuilder.toString(), sort, params.toArray());
        }

        return panacheQuery.page(query.getPage(), query.getSize())
                .list()
                .stream()
                .map(VehicleMapper::toDomain)
                .toList();
    }

    private Sort createSort(String sortParam) {
        String[] sortFields = sortParam.split(",");
        Sort sort = null;

        for (String sortField : sortFields) {
            sortField = sortField.trim();
            if (sortField.startsWith("-")) {
                // Descending order
                String fieldName = sortField.substring(1);
                if (sort == null) {
                    sort = Sort.by(fieldName, Sort.Direction.Descending);
                } else {
                    sort.and(fieldName, Sort.Direction.Descending);
                }
            } else if (sortField.startsWith("+")) {
                // Ascending order (explicit)
                String fieldName = sortField.substring(1);
                if (sort == null) {
                    sort = Sort.by(fieldName, Sort.Direction.Ascending);
                } else {
                    sort.and(fieldName, Sort.Direction.Ascending);
                }
            } else {
                // Default ascending order
                if (sort == null) {
                    sort = Sort.by(sortField, Sort.Direction.Ascending);
                } else {
                    sort.and(sortField, Sort.Direction.Ascending);
                }
            }
        }

        return sort;
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
}
