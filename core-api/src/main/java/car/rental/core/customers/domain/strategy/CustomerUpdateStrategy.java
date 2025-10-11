package car.rental.core.customers.domain.strategy;

import car.rental.core.customers.domain.model.Customer;
import car.rental.core.customers.dto.UpdateCustomerRequest;

public interface CustomerUpdateStrategy {

    Customer updateCustomer(Long id, UpdateCustomerRequest request);
}
