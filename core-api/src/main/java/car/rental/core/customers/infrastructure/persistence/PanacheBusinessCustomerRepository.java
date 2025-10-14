package car.rental.core.customers.infrastructure.persistence;

import car.rental.core.common.exception.ResourceNotFoundException;
import car.rental.core.customers.domain.model.Customer;
import car.rental.core.customers.domain.repository.CustomerRepository;
import car.rental.core.customers.infrastructure.mapper.BusinessCustomerMapper;
import car.rental.core.customers.infrastructure.mapper.CustomerProfileMapper;
import car.rental.core.users.infrastructure.persistence.UserEntity;
import car.rental.core.users.infrastructure.persistence.UserEntityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class PanacheBusinessCustomerRepository implements CustomerRepository {

    private final BusinessCustomerEntityRepository entityRepository;
    private final UserEntityRepository userEntityRepository;
    private final CustomerProfileEntityRepository customerProfileEntityRepository;

    @Override
    public Optional<Customer> findById(Long id) {
        return entityRepository.findByIdOptional(id).map(BusinessCustomerMapper::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return entityRepository.listAll().stream().map(BusinessCustomerMapper::toDomain).toList();
    }

    @Transactional
    @Override
    public Customer save(Customer customer) {
        // Fetch managed UserEntity by id
        UserEntity userEntity = userEntityRepository.findById(customer.getId());
        if (userEntity == null) {
            throw new IllegalStateException("UserEntity not found for id: " + customer.getId());
        }
        CustomerProfileEntity customerProfileEntity = CustomerProfileMapper.toEntity(customer, userEntity);
        customerProfileEntityRepository.persist(customerProfileEntity);
        // Create PrivateCustomerEntity and set managed CustomerProfileEntity
        BusinessCustomerEntity entity = BusinessCustomerMapper.toEntity(customer, customerProfileEntity);
        entityRepository.persist(entity);
        return BusinessCustomerMapper.toDomain(entity);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        entityRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Customer update(Customer customer) {
        // Hybrid approach: fetch managed entity first, then update fields
        BusinessCustomerEntity entity = entityRepository.findById(customer.getId());
        if (entity == null) {
            throw new ResourceNotFoundException("Customer not found: " + customer.getId());
        }
        BusinessCustomerMapper.updateEntity(entity, customer); // copies domain state → managed entity
        entityRepository.persist(entity);
        return BusinessCustomerMapper.toDomain(entity);
    }

    public void persist(BusinessCustomerEntity entity) {
        entityRepository.persist(entity);
    }
}
