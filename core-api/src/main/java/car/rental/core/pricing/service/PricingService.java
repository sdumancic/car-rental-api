package car.rental.core.pricing.service;

import car.rental.core.pricing.domain.model.Pricing;
import car.rental.core.pricing.domain.model.PricingTier;
import car.rental.core.pricing.dto.CreatePricingRequest;
import car.rental.core.pricing.infrastructure.mapper.PricingMapper;
import car.rental.core.pricing.infrastructure.persistence.PanachePricingRepository;
import car.rental.core.vehicle.domain.model.Vehicle;
import car.rental.core.vehicle.service.VehicleService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class PricingService {

    private final PanachePricingRepository panachePricingRepository;

    private final VehicleService vehicleService;

    @Transactional
    public Pricing createPricing(CreatePricingRequest request) {
        Vehicle vehicle = vehicleService.findVehicleById(request.getVehicleId());
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle not found");
        }
        Pricing pricing = PricingMapper.toDomain(request, vehicle);
        return panachePricingRepository.save(pricing);
    }

    public Pricing findPricingById(Long id) {
        return panachePricingRepository.findById(id).orElse(null);
    }

    public List<Pricing> findPricingByVehicleId(Long vehicleId) {
        return panachePricingRepository.findByVehicleId(vehicleId);
    }

    public Pricing findActivePricingByVehicleId(Long vehicleId) {
        return panachePricingRepository.findActiveByVehicleId(vehicleId).orElse(null);
    }

    @Transactional
    public Pricing updatePricing(Pricing pricing) {
        return panachePricingRepository.update(pricing);
    }

    @Transactional
    public void deletePricingById(Long id) {
        panachePricingRepository.softDeleteById(id);
    }

    public BigDecimal calculatePrice(Long vehicleId, int rentalDays) {
        Pricing pricing = findActivePricingByVehicleId(vehicleId);
        if (pricing == null || pricing.getPricingCategory() == null) {
            return null;
        }
        for (PricingTier tier : pricing.getPricingCategory().getPricingTiers()) {
            if (rentalDays >= tier.getMinDays() && (tier.getMaxDays() == null || rentalDays <= tier.getMaxDays())) {
                return tier.getPrice();
            }
        }
        return null; // No matching tier
    }
}
