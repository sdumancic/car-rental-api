package car.rental.core.vehicle.domain.repository;

import car.rental.core.common.domain.BaseRepository;
import car.rental.core.vehicle.domain.model.Vehicle;
import car.rental.core.vehicle.dto.QueryVehicleRequest;

import java.util.List;

public interface VehicleRepository extends BaseRepository<Vehicle> {
    List<Vehicle> findByQuery(QueryVehicleRequest query);

    long countByQuery(QueryVehicleRequest query);
}
