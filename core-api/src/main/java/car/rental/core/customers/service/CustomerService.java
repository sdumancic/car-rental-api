package car.rental.core.customers.service;

import car.rental.core.customers.domain.model.Customer;
import car.rental.core.customers.domain.model.CustomerType;
import car.rental.core.customers.dto.CreateCustomerRequest;
import car.rental.core.customers.infrastructure.mapper.BusinessCustomerMapper;
import car.rental.core.customers.infrastructure.mapper.PrivateCustomerMapper;
import car.rental.core.customers.infrastructure.persistence.PanacheBusinessCustomerRepository;
import car.rental.core.customers.infrastructure.persistence.PanachePrivateCustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class CustomerService {

    private final PanachePrivateCustomerRepository privateCustomerRepository;
    private final PanacheBusinessCustomerRepository businessCustomerRepository;

    @Transactional
    public Customer createCustomer(CreateCustomerRequest request) {
        if (request.getCustomerType() == null) {
            throw new IllegalArgumentException("Customer type must be provided");
        }
        Customer customer = null;
        if (request.getCustomerType() == CustomerType.PRIVATE) {
            customer = PrivateCustomerMapper.toDomain(request);
            return privateCustomerRepository.save(customer);
        } else if (request.getCustomerType() == CustomerType.BUSINESS) {
            customer = BusinessCustomerMapper.toDomain(request);
            return businessCustomerRepository.save(customer);
        }
        throw new IllegalArgumentException("Unsupported customer type: " + request.getCustomerType());
    }
}
