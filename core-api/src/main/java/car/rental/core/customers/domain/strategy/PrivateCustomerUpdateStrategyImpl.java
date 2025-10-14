package car.rental.core.customers.domain.strategy;

import car.rental.core.customers.domain.model.Customer;
import car.rental.core.customers.domain.model.CustomerType;
import car.rental.core.customers.dto.UpdateCustomerRequest;
import car.rental.core.customers.infrastructure.persistence.PanacheCustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@CustomerTypeQualifier(CustomerType.PRIVATE)
@RequiredArgsConstructor
public class PrivateCustomerUpdateStrategyImpl implements CustomerUpdateStrategy {

    private final PanacheCustomerRepository customerRepository;

    @Override
    public Customer updateCustomer(Long id, UpdateCustomerRequest request) {
        Customer existing = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        existing.setCustomerType(request.getCustomerType());
        existing.setDateOfBirth(request.getDateOfBirth());
        existing.setDriverLicenseNo(request.getDriverLicenseNo());
        existing.setCompanyName(null);
        existing.setTaxNumber(null);
        existing.setRegistrationNumber(null);
        return customerRepository.update(existing);
    }
}
