package car.rental.core.pricing.infrastructure.persistence;

import car.rental.core.pricing.domain.model.PricingCategory;
import car.rental.core.pricing.domain.repository.PricingCategoryRepository;
import car.rental.core.pricing.infrastructure.PricingCategoryEntity;
import car.rental.core.pricing.infrastructure.mapper.PricingMapper;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PanachePricingCategoryRepository implements PricingCategoryRepository {

    private final PricingCategoryEntityRepository pricingCategoryEntityRepository;

    @Override
    public Optional<PricingCategory> findById(Long id) {
        return pricingCategoryEntityRepository.findByIdOptional(id).map(PricingMapper::toPricingCategoryDomain);
    }

    @Override
    public List<PricingCategory> findAll() {
        return pricingCategoryEntityRepository.listAll().stream().map(PricingMapper::toPricingCategoryDomain).toList();
    }

    @Override
    public PricingCategory save(PricingCategory pricingCategory) {
        PricingCategoryEntity entity = PricingMapper.toPricingCategoryEntity(pricingCategory);
        if (entity.getId() == null) {
            entity.setDateCreated(Instant.now());
        }
        entity.setDateModified(Instant.now());
        pricingCategoryEntityRepository.persist(entity);
        return PricingMapper.toPricingCategoryDomain(entity);
    }

    @Override
    public void deleteById(Long id) {
        pricingCategoryEntityRepository.deleteById(id);
    }

    @Override
    public Optional<PricingCategory> findByName(String name) {
        return pricingCategoryEntityRepository.find("name = :name", Parameters.with("name", name))
                .firstResultOptional().map(PricingMapper::toPricingCategoryDomain);
    }

    @Override
    public PricingCategory update(PricingCategory pricingCategory) {
        PricingCategoryEntity entity = PricingMapper.toPricingCategoryEntity(pricingCategory);
        entity.setDateModified(Instant.now());
        pricingCategoryEntityRepository.persist(entity);
        return PricingMapper.toPricingCategoryDomain(entity);
    }

    @Override
    public void softDeleteById(Long id) {
        Optional<PricingCategoryEntity> entityOpt = pricingCategoryEntityRepository.findByIdOptional(id);
        if (entityOpt.isPresent()) {
            PricingCategoryEntity entity = entityOpt.get();
            entity.setActive(false);
            entity.setDateModified(Instant.now());
            pricingCategoryEntityRepository.persist(entity);
        }
    }
}
