package car.rental.core.customers.service;

import car.rental.core.customers.domain.model.Customer;
import car.rental.core.customers.domain.model.CustomerType;
import car.rental.core.customers.domain.strategy.CustomerCreationStrategy;
import car.rental.core.customers.domain.strategy.CustomerUpdateStrategy;
import car.rental.core.customers.dto.CreateCustomerRequest;
import car.rental.core.customers.dto.UpdateCustomerRequest;
import car.rental.core.customers.infrastructure.mapper.BusinessCustomerMapper;
import car.rental.core.customers.infrastructure.mapper.PrivateCustomerMapper;
import car.rental.core.customers.infrastructure.persistence.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final Map<CustomerType, CustomerCreationStrategy> customerCreationStrategies;
    private final Map<CustomerType, CustomerUpdateStrategy> customerUpdateStrategies;

    private final CustomerProfileEntityRepository customerProfileEntityRepository;
    private final PanacheCustomerRepository panacheCustomerRepository;
    private final PanacheBusinessCustomerRepository panacheBusinessCustomerRepository;
    private final PanachePrivateCustomerRepository panachePrivateCustomerRepository;

    @Transactional
    public Customer createCustomer(CreateCustomerRequest request) {
        log.info("CustomerCreationStrategies {} ", customerCreationStrategies.size());
        if (request.getCustomerType() == null) {
            throw new IllegalArgumentException("Customer type must be provided");
        }
        for (Map.Entry<CustomerType, CustomerCreationStrategy> entry : customerCreationStrategies.entrySet()) {
            log.info("Registered strategy for customer type: {}", entry.getKey());
        }
        CustomerCreationStrategy strategy = customerCreationStrategies.get(request.getCustomerType());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported customer type: " + request.getCustomerType());
        }
        return strategy.createCustomer(request);
    }

    @Transactional
    public Customer updateCustomer(Long id, UpdateCustomerRequest request) {
        if (request.getCustomerType() == null) {
            throw new IllegalArgumentException("Customer type must be provided");
        }
        Customer existing = panacheCustomerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        if (existing.getCustomerType() != request.getCustomerType()) {
            // Type change: delete old entity and create new one
            if (existing.getCustomerType() == CustomerType.BUSINESS) {
                panacheBusinessCustomerRepository.deleteById(id);
            } else if (existing.getCustomerType() == CustomerType.PRIVATE) {
                panachePrivateCustomerRepository.deleteById(id);
            }
            // Find existing CustomerProfileEntity
            CustomerProfileEntity profile = customerProfileEntityRepository.findById(id);
            if (profile == null) {
                throw new IllegalStateException("CustomerProfileEntity not found for id: " + id);
            }
            // Create domain from request and existing
            Customer newCustomer = Customer.builder()
                    .id(id)
                    .customerType(request.getCustomerType())
                    .dateOfBirth(request.getDateOfBirth() != null ? request.getDateOfBirth() : existing.getDateOfBirth())
                    .driverLicenseNo(request.getDriverLicenseNo() != null ? request.getDriverLicenseNo() : existing.getDriverLicenseNo())
                    .companyName(request.getCompanyName() != null ? request.getCompanyName() : existing.getCompanyName())
                    .taxNumber(request.getTaxNumber() != null ? request.getTaxNumber() : existing.getTaxNumber())
                    .registrationNumber(request.getRegistrationNumber() != null ? request.getRegistrationNumber() : existing.getRegistrationNumber())
                    .build();
            // Create and save new specific entity
            if (request.getCustomerType() == CustomerType.PRIVATE) {
                PrivateCustomerEntity entity = PrivateCustomerMapper.toEntity(newCustomer, profile);
                panachePrivateCustomerRepository.persist(entity);
                return PrivateCustomerMapper.toDomain(entity);
            } else {
                BusinessCustomerEntity entity = BusinessCustomerMapper.toEntity(newCustomer, profile);
                panacheBusinessCustomerRepository.persist(entity);
                return BusinessCustomerMapper.toDomain(entity);
            }
        } else {
            // Same type: use update strategy
            CustomerUpdateStrategy strategy = customerUpdateStrategies.get(request.getCustomerType());
            if (strategy == null) {
                throw new IllegalArgumentException("Unsupported customer type: " + request.getCustomerType());
            }
            return strategy.updateCustomer(id, request);
        }
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerProfileEntityRepository.findByIdOptional(id).isPresent()) {
            throw new IllegalArgumentException("Customer not found");
        }
        customerProfileEntityRepository.deleteById(id);
    }


    public Customer findCustomerById(Long id) {
        return panacheCustomerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }
}
