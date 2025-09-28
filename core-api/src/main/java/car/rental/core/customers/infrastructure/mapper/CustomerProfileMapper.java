package car.rental.core.customers.infrastructure.mapper;

import car.rental.core.customers.domain.model.Customer;
import car.rental.core.customers.infrastructure.persistence.CustomerProfileEntity;
import car.rental.core.users.infrastructure.persistence.UserEntity;

public class CustomerProfileMapper {

    // --- Domain → Entity ---
    public static CustomerProfileEntity toEntity(Customer domain, UserEntity userEntity) {
        if (domain == null) {
            return null;
        }
        CustomerProfileEntity entity = new CustomerProfileEntity();
        entity.setId(domain.getId());
        entity.setCustomerType(domain.getCustomerType());
        entity.setUser(userEntity);
        return entity;
    }

}
