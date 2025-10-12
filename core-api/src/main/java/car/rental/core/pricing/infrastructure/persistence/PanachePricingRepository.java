package car.rental.core.pricing.infrastructure.persistence;

import car.rental.core.pricing.domain.model.Pricing;
import car.rental.core.pricing.domain.repository.PricingRepository;
import car.rental.core.pricing.infrastructure.PricingEntity;
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
public class PanachePricingRepository implements PricingRepository {

    private final PricingEntityRepository pricingEntityRepository;

    @Override
    public Optional<Pricing> findById(Long id) {
        return pricingEntityRepository.findByIdOptional(id).map(PricingMapper::toDomain);
    }

    @Override
    public List<Pricing> findAll() {
        return pricingEntityRepository.listAll().stream().map(PricingMapper::toDomain).toList();
    }

    @Override
    public Pricing save(Pricing pricing) {
        PricingEntity entity = PricingMapper.toEntity(pricing);
        if (entity.getId() == null) {
            entity.setDateCreated(Instant.now());
        }
        entity.setDateModified(Instant.now());
        pricingEntityRepository.persist(entity);
        return PricingMapper.toDomain(entity);
    }

    @Override
    public void deleteById(Long id) {
        pricingEntityRepository.deleteById(id);
    }

    @Override
    public List<Pricing> findByVehicleId(Long vehicleId) {
        return pricingEntityRepository.find("vehicle.id = :vehicleId", Parameters.with("vehicleId", vehicleId))
                .stream().map(PricingMapper::toDomain).toList();
    }

    @Override
    public Optional<Pricing> findActiveByVehicleId(Long vehicleId) {
        return pricingEntityRepository.find("vehicle.id = :vehicleId and active = true", Parameters.with("vehicleId", vehicleId))
                .firstResultOptional().map(PricingMapper::toDomain);
    }

    @Override
    public Pricing update(Pricing pricing) {
        PricingEntity entity = PricingMapper.toEntity(pricing);
        entity.setDateModified(Instant.now());
        pricingEntityRepository.persist(entity);
        return PricingMapper.toDomain(entity);
    }

    @Override
    public void softDeleteById(Long id) {
        Optional<PricingEntity> entityOpt = pricingEntityRepository.findByIdOptional(id);
        if (entityOpt.isPresent()) {
            PricingEntity entity = entityOpt.get();
            entity.setActive(false);
            entity.setDateModified(Instant.now());
            pricingEntityRepository.persist(entity);
        }
    }
}
