package car.rental.core.pricing.domain.repository;

import car.rental.core.common.domain.BaseRepository;
import car.rental.core.pricing.domain.model.Pricing;

import java.util.List;
import java.util.Optional;

public interface PricingRepository extends BaseRepository<Pricing> {
    List<Pricing> findByVehicleId(Long vehicleId);

    Optional<Pricing> findActiveByVehicleId(Long vehicleId);

    Pricing update(Pricing pricing);

    void softDeleteById(Long id);

}
