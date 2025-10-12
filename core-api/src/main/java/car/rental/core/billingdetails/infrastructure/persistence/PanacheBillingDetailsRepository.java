package car.rental.core.billingdetails.infrastructure.persistence;

import car.rental.core.billingdetails.domain.model.BillingDetails;
import car.rental.core.billingdetails.domain.repository.BillingDetailsRepository;
import car.rental.core.billingdetails.infrastructure.BillingDetailsEntity;
import car.rental.core.billingdetails.infrastructure.mapper.BillingDetailsMapper;
import car.rental.core.common.exception.ResourceNotFoundException;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PanacheBillingDetailsRepository implements BillingDetailsRepository {

    private final BillingDetailsEntityRepository billingDetailsEntityRepository;

    @Override
    public Optional<BillingDetails> findById(Long id) {
        return billingDetailsEntityRepository.findByIdOptional(id).map(BillingDetailsMapper::toDomain);
    }

    @Override
    public List<BillingDetails> findAll() {
        return billingDetailsEntityRepository.listAll().stream().map(BillingDetailsMapper::toDomain).toList();
    }

    @Transactional
    @Override
    public BillingDetails save(BillingDetails billingDetails) {
        BillingDetailsEntity entity = BillingDetailsMapper.toEntity(billingDetails);
        if (entity.getId() == null) {
            entity.setDateCreated(Instant.now());
        }
        entity.setDateModified(Instant.now());
        billingDetailsEntityRepository.persist(entity);
        return BillingDetailsMapper.toDomain(entity);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        billingDetailsEntityRepository.deleteById(id);
    }

    @Override
    public List<BillingDetails> findByUserId(Long userId) {
        return billingDetailsEntityRepository.find("user.id = :userId and active = true", Parameters.with("userId", userId))
                .stream().map(BillingDetailsMapper::toDomain).toList();
    }

    @Transactional
    @Override
    public BillingDetails update(BillingDetails billingDetails) {
        // Hybrid approach: fetch managed entity first, then update fields
        BillingDetailsEntity entity = billingDetailsEntityRepository.findById(billingDetails.getId());
        if (entity == null) {
            throw new ResourceNotFoundException("BillingDetails not found: " + billingDetails.getId());
        }
        BillingDetailsMapper.updateEntity(entity, billingDetails); // copies domain state → managed entity
        billingDetailsEntityRepository.persist(entity);
        return BillingDetailsMapper.toDomain(entity);
    }

    @Transactional
    @Override
    public void softDeleteById(Long id) {
        Optional<BillingDetailsEntity> entityOpt = billingDetailsEntityRepository.findByIdOptional(id);
        if (entityOpt.isPresent()) {
            BillingDetailsEntity entity = entityOpt.get();
            entity.setActive(false);
            entity.setDateModified(Instant.now());
            billingDetailsEntityRepository.persist(entity);
        }
    }
}
