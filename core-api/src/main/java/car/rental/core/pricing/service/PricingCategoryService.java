package car.rental.core.pricing.service;

import car.rental.core.pricing.domain.model.PricingCategory;
import car.rental.core.pricing.dto.CreatePricingCategoryRequest;
import car.rental.core.pricing.infrastructure.mapper.PricingMapper;
import car.rental.core.pricing.infrastructure.persistence.PanachePricingCategoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class PricingCategoryService {

    private final PanachePricingCategoryRepository panachePricingCategoryRepository;

    @Transactional
    public PricingCategory createPricingCategory(CreatePricingCategoryRequest request) {
        if (panachePricingCategoryRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Pricing category with name '" + request.getName() + "' already exists");
        }
        PricingCategory pricingCategory = PricingMapper.toPricingCategoryDomain(request);
        return panachePricingCategoryRepository.save(pricingCategory);
    }

    public PricingCategory findPricingCategoryById(Long id) {
        return panachePricingCategoryRepository.findById(id).orElse(null);
    }

    public List<PricingCategory> findAllPricingCategories() {
        return panachePricingCategoryRepository.findAll();
    }

    @Transactional
    public PricingCategory updatePricingCategory(Long id, CreatePricingCategoryRequest request) {
        PricingCategory existing = panachePricingCategoryRepository.findById(id).orElse(null);
        if (existing == null) {
            throw new IllegalArgumentException("Pricing category not found");
        }
        // Check if name is being changed and if it conflicts
        if (!existing.getName().equals(request.getName()) &&
                panachePricingCategoryRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Pricing category with name '" + request.getName() + "' already exists");
        }
        PricingCategory updated = PricingCategory.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .pricingTiers(request.getPricingTiers().stream()
                        .map(tier -> car.rental.core.pricing.domain.model.PricingTier.builder()
                                .minDays(tier.getMinDays())
                                .maxDays(tier.getMaxDays())
                                .price(tier.getPrice())
                                .build())
                        .collect(java.util.stream.Collectors.toList()))
                .active(existing.getActive())
                .dateCreated(existing.getDateCreated())
                .dateModified(existing.getDateModified())
                .build();
        return panachePricingCategoryRepository.update(updated);
    }

    @Transactional
    public void deletePricingCategoryById(Long id) {
        panachePricingCategoryRepository.softDeleteById(id);
    }
}
