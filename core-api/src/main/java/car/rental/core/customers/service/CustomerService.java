package car.rental.core.customers.service;

import car.rental.core.customers.domain.model.Customer;
import car.rental.core.customers.domain.model.CustomerType;
import car.rental.core.customers.domain.strategy.CustomerCreationStrategy;
import car.rental.core.customers.dto.CreateCustomerRequest;
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

    @Transactional
    public Customer createCustomer(CreateCustomerRequest request) {
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
}
