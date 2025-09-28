package car.rental.core.customers.domain.strategy;

import car.rental.core.customers.domain.model.Customer;
import car.rental.core.customers.domain.model.CustomerType;
import car.rental.core.customers.dto.CreateCustomerRequest;
import car.rental.core.customers.infrastructure.mapper.BusinessCustomerMapper;
import car.rental.core.customers.infrastructure.persistence.PanacheBusinessCustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@CustomerTypeQualifier(CustomerType.BUSINESS)
@RequiredArgsConstructor
public class BusinessCustomerCreationStrategyImpl implements CustomerCreationStrategy {

    private final PanacheBusinessCustomerRepository businessCustomerRepository;

    @Override
    public Customer createCustomer(CreateCustomerRequest request) {

        Customer customer = BusinessCustomerMapper.toDomain(request);
        return businessCustomerRepository.save(customer);
    }
}
