package car.rental.core.pricing.infrastructure.mapper;

import car.rental.core.pricing.domain.model.Pricing;
import car.rental.core.pricing.domain.model.PricingCategory;
import car.rental.core.pricing.domain.model.PricingTier;
import car.rental.core.pricing.dto.CreatePricingCategoryRequest;
import car.rental.core.pricing.dto.CreatePricingRequest;
import car.rental.core.pricing.dto.PricingTierRequest;
import car.rental.core.pricing.infrastructure.PricingCategoryEntity;
import car.rental.core.pricing.infrastructure.PricingEntity;
import car.rental.core.pricing.infrastructure.PricingTierEntity;
import car.rental.core.vehicle.domain.model.Vehicle;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class PricingMapper {
    // --- API → Domain ---
    public static Pricing toDomain(CreatePricingRequest request, Vehicle vehicle) {
        if (request == null) {
            return null;
        }
        List<PricingTier> pricingTiers = request.getPricingTiers().stream()
                .map(PricingMapper::toPricingTierDomain)
                .collect(Collectors.toList());
        PricingCategory pricingCategory = PricingCategory.builder()
                .id(null)
                .name(request.getCategoryName())
                .description(request.getCategoryDescription())
                .pricingTiers(pricingTiers)
                .active(true)
                .dateCreated(Instant.now())
                .dateModified(Instant.now())
                .build();
        return Pricing.builder()
                .id(null)
                .vehicle(vehicle)
                .pricingCategory(pricingCategory)
                .active(true)
                .dateCreated(Instant.now())
                .dateModified(Instant.now())
                .build();
    }

    public static PricingCategory toPricingCategoryDomain(CreatePricingCategoryRequest request) {
        if (request == null) {
            return null;
        }
        List<PricingTier> pricingTiers = request.getPricingTiers().stream()
                .map(PricingMapper::toPricingTierDomain)
                .collect(Collectors.toList());
        return PricingCategory.builder()
                .id(null)
                .name(request.getName())
                .description(request.getDescription())
                .pricingTiers(pricingTiers)
                .active(true)
                .dateCreated(Instant.now())
                .dateModified(Instant.now())
                .build();
    }

    private static PricingTier toPricingTierDomain(PricingTierRequest request) {
        return PricingTier.builder()
                .minDays(request.getMinDays())
                .maxDays(request.getMaxDays())
                .price(request.getPrice())
                .build();
    }

    // --- Domain → Entity ---
    public static PricingEntity toEntity(Pricing domain) {
        if (domain == null) {
            return null;
        }
        PricingEntity entity = new PricingEntity();
        entity.setId(domain.getId());
        entity.setVehicle(null); // Set separately if needed
        entity.setPricingCategory(toPricingCategoryEntity(domain.getPricingCategory()));
        entity.setActive(domain.getActive());
        entity.setDateCreated(domain.getDateCreated());
        entity.setDateModified(domain.getDateModified());
        return entity;
    }

    // --- PricingCategory Mapping ---
    public static PricingCategoryEntity toPricingCategoryEntity(PricingCategory domain) {
        if (domain == null) {
            return null;
        }
        PricingCategoryEntity entity = new PricingCategoryEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPricingTiers(domain.getPricingTiers().stream()
                .map(tier -> toPricingTierEntity(tier, entity))
                .collect(Collectors.toList()));
        entity.setActive(domain.getActive());
        entity.setDateCreated(domain.getDateCreated());
        entity.setDateModified(domain.getDateModified());
        return entity;
    }

    public static PricingCategory toPricingCategoryDomain(PricingCategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        List<PricingTier> pricingTiers = entity.getPricingTiers().stream()
                .map(PricingMapper::toPricingTierDomain)
                .collect(Collectors.toList());
        return PricingCategory.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .pricingTiers(pricingTiers)
                .active(entity.getActive())
                .dateCreated(entity.getDateCreated())
                .dateModified(entity.getDateModified())
                .build();
    }

    private static PricingTierEntity toPricingTierEntity(PricingTier domain, PricingCategoryEntity pricingCategoryEntity) {
        PricingTierEntity entity = new PricingTierEntity();
        entity.setId(null); // New entity
        entity.setPricingCategory(pricingCategoryEntity);
        entity.setMinDays(domain.getMinDays());
        entity.setMaxDays(domain.getMaxDays());
        entity.setPrice(domain.getPrice());
        return entity;
    }

    // --- Entity → Domain ---
    public static Pricing toDomain(PricingEntity entity) {
        if (entity == null) {
            return null;
        }
        PricingCategory pricingCategory = toPricingCategoryDomain(entity.getPricingCategory());
        return Pricing.builder()
                .id(entity.getId())
                .vehicle(null // Set separately if needed
                )
                .pricingCategory(pricingCategory)
                .active(entity.getActive())
                .dateCreated(entity.getDateCreated())
                .dateModified(entity.getDateModified())
                .build();
    }

    private static PricingTier toPricingTierDomain(PricingTierEntity entity) {
        return PricingTier.builder()
                .minDays(entity.getMinDays())
                .maxDays(entity.getMaxDays())
                .price(entity.getPrice())
                .build();
    }
}
