package car.rental.core.pricing.service;

import car.rental.core.pricing.domain.model.PricingCategory;
import car.rental.core.pricing.domain.repository.PricingCategoryRepository;
import car.rental.core.pricing.dto.CreatePricingCategoryRequest;
import car.rental.core.pricing.infrastructure.mapper.PricingMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class PricingCategoryService {

    private final PricingCategoryRepository pricingCategoryRepository;

    @Transactional
    public PricingCategory createPricingCategory(CreatePricingCategoryRequest request) {
        if (pricingCategoryRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Pricing category with name '" + request.getName() + "' already exists");
        }
        PricingCategory pricingCategory = PricingMapper.toPricingCategoryDomain(request);
        return pricingCategoryRepository.save(pricingCategory);
    }

    public PricingCategory findPricingCategoryById(Long id) {
        return pricingCategoryRepository.findById(id).orElse(null);
    }

    public List<PricingCategory> findAllPricingCategories() {

        return pricingCategoryRepository.findAll();
    }

    public PricingCategory updatePricingCategory(Long id, CreatePricingCategoryRequest request) {
        PricingCategory pricingCategory = PricingMapper.toPricingCategoryDomain(request);
        pricingCategory.setId(id);
        return pricingCategoryRepository.update(pricingCategory);
    }

    public void deletePricingCategoryById(Long id) {
        pricingCategoryRepository.softDeleteById(id);
    }
}
