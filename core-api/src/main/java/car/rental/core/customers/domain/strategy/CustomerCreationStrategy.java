package car.rental.core.customers.domain.strategy;

import car.rental.core.customers.domain.model.Customer;
import car.rental.core.customers.dto.CreateCustomerRequest;

public interface CustomerCreationStrategy {

    Customer createCustomer(CreateCustomerRequest request);
}
