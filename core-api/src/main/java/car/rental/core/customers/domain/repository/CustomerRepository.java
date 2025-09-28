package car.rental.core.customers.domain.repository;

import car.rental.core.customers.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    Optional<Customer> findById(Long id);

    List<Customer> findAll();

    Customer save(Customer customer);

    void deleteById(Long id);
}
