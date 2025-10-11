package car.rental.core.customers.infrastructure.persistence;

import car.rental.core.customers.domain.model.Customer;
import car.rental.core.customers.domain.model.CustomerType;
import car.rental.core.customers.domain.repository.CustomerRepository;
import car.rental.core.customers.infrastructure.mapper.BusinessCustomerMapper;
import car.rental.core.customers.infrastructure.mapper.CustomerProfileMapper;
import car.rental.core.customers.infrastructure.mapper.PrivateCustomerMapper;
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
public class PanacheCustomerRepository implements CustomerRepository {

    private final CustomerProfileEntityRepository customerProfileEntityRepository;
    private final PrivateCustomerEntityRepository privateCustomerEntityRepository;
    private final BusinessCustomerEntityRepository businessCustomerEntityRepository;
    private final UserEntityRepository userEntityRepository;

    @Override
    public Optional<Customer> findById(Long id) {
        Optional<CustomerProfileEntity> profileOpt = customerProfileEntityRepository.findByIdOptional(id);
        if (profileOpt.isEmpty()) {
            return Optional.empty();
        }
        CustomerProfileEntity profile = profileOpt.get();
        if (profile.getPrivateCustomer() != null) {
            return Optional.of(PrivateCustomerMapper.toDomain(profile.getPrivateCustomer()));
        } else if (profile.getBusinessCustomer() != null) {
            return Optional.of(BusinessCustomerMapper.toDomain(profile.getBusinessCustomer()));
        } else {
            // invalid state
            return Optional.empty();
        }
    }

    @Override
    public List<Customer> findAll() {
        return customerProfileEntityRepository.listAll().stream()
                .map(profile -> {
                    if (profile.getPrivateCustomer() != null) {
                        return PrivateCustomerMapper.toDomain(profile.getPrivateCustomer());
                    } else if (profile.getBusinessCustomer() != null) {
                        return BusinessCustomerMapper.toDomain(profile.getBusinessCustomer());
                    } else {
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Override
    @Transactional
    public Customer save(Customer customer) {
        // Fetch managed UserEntity by id
        UserEntity userEntity = userEntityRepository.findById(customer.getId());
        if (userEntity == null) {
            throw new IllegalStateException("UserEntity not found for id: " + customer.getId());
        }

        // Check if profile exists
        CustomerProfileEntity profile = customerProfileEntityRepository.findByIdOptional(customer.getId()).orElse(null);
        if (profile == null) {
            // create new
            profile = CustomerProfileMapper.toEntity(customer, userEntity);
            customerProfileEntityRepository.persist(profile);
        } else {
            // update profile
            profile.setCustomerType(customer.getCustomerType());
            // check if type changed
            CustomerType currentType = profile.getCustomerType();
            if (!currentType.equals(customer.getCustomerType())) {
                // type changed, delete old specific
                if (currentType == CustomerType.PRIVATE) {
                    privateCustomerEntityRepository.deleteById(customer.getId());
                    profile.setPrivateCustomer(null);
                } else if (currentType == CustomerType.BUSINESS) {
                    businessCustomerEntityRepository.deleteById(customer.getId());
                    profile.setBusinessCustomer(null);
                }
            }
        }

        // now create or update specific
        if (customer.getCustomerType() == CustomerType.PRIVATE) {
            PrivateCustomerEntity entity = PrivateCustomerMapper.toEntity(customer, profile);
            privateCustomerEntityRepository.persist(entity);
            return PrivateCustomerMapper.toDomain(entity);
        } else if (customer.getCustomerType() == CustomerType.BUSINESS) {
            BusinessCustomerEntity entity = BusinessCustomerMapper.toEntity(customer, profile);
            businessCustomerEntityRepository.persist(entity);
            return BusinessCustomerMapper.toDomain(entity);
        } else {
            throw new IllegalArgumentException("Unsupported customer type: " + customer.getCustomerType());
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        customerProfileEntityRepository.deleteById(id);
    }
}
