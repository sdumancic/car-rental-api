package car.rental.core.pricing.service;

import car.rental.core.common.exception.ResourceNotFoundException;
import car.rental.core.pricing.domain.model.Pricing;
import car.rental.core.pricing.domain.model.PricingCategory;
import car.rental.core.pricing.domain.model.PricingTier;
import car.rental.core.pricing.domain.repository.PricingCategoryRepository;
import car.rental.core.pricing.domain.repository.PricingRepository;
import car.rental.core.pricing.dto.CreatePricingRequest;
import car.rental.core.pricing.infrastructure.mapper.PricingMapper;
import car.rental.core.vehicle.domain.model.Vehicle;
import car.rental.core.vehicle.service.VehicleService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class PricingService {

    private final PricingRepository pricingRepository;
    private final PricingCategoryRepository pricingCategoryRepository;
    private final VehicleService vehicleService;

    public Pricing createPricing(CreatePricingRequest request) {
        Vehicle vehicle = vehicleService.findVehicleById(request.getVehicleId());
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle not found");
        }
        PricingCategory pricingCategory = pricingCategoryRepository.findById(request.getPricingCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("PricingCategory not found"));
        Pricing pricing = PricingMapper.toDomain(request, vehicle, pricingCategory);
        return pricingRepository.save(pricing);
    }

    public Pricing findPricingById(Long id) {
        return pricingRepository.findById(id).orElse(null);
    }

    public List<Pricing> findPricingByVehicleId(Long vehicleId) {
        return pricingRepository.findByVehicleId(vehicleId);
    }

    public Pricing findActivePricingByVehicleId(Long vehicleId) {
        return pricingRepository.findActiveByVehicleId(vehicleId).orElse(null);
    }

    public Pricing updatePricing(Long id, CreatePricingRequest request) {
        Vehicle vehicle = vehicleService.findVehicleById(request.getVehicleId());
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle not found");
        }
        PricingCategory pricingCategory = pricingCategoryRepository.findById(request.getPricingCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("PricingCategory not found"));
        Pricing pricing = PricingMapper.toDomain(request, vehicle, pricingCategory);
        pricing.setId(id);
        return pricingRepository.update(pricing);
    }

    public void deletePricingById(Long id) {
        pricingRepository.softDeleteById(id);
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
