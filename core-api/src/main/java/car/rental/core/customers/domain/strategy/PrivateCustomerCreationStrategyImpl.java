package car.rental.core.customers.domain.strategy;

import car.rental.core.customers.domain.model.Customer;
import car.rental.core.customers.domain.model.CustomerType;
import car.rental.core.customers.dto.CreateCustomerRequest;
import car.rental.core.customers.infrastructure.mapper.PrivateCustomerMapper;
import car.rental.core.customers.infrastructure.persistence.PanachePrivateCustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@CustomerTypeQualifier(CustomerType.PRIVATE)
@RequiredArgsConstructor
public class PrivateCustomerCreationStrategyImpl implements CustomerCreationStrategy {

    private final PanachePrivateCustomerRepository privateCustomerRepository;

    @Override
    public Customer createCustomer(CreateCustomerRequest request) {
        Customer customer = PrivateCustomerMapper.toDomain(request);
        return privateCustomerRepository.save(customer);
    }
}
