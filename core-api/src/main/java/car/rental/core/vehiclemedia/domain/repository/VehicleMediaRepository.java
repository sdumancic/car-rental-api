package car.rental.core.vehiclemedia.domain.repository;

import car.rental.core.common.domain.BaseRepository;
import car.rental.core.vehiclemedia.domain.model.VehicleMedia;

import java.util.List;

public interface VehicleMediaRepository extends BaseRepository<VehicleMedia> {

    List<VehicleMedia> findAllForVehicle(Long vehicleId);

    VehicleMedia updateUrl(VehicleMedia vehicleMedia);
}
