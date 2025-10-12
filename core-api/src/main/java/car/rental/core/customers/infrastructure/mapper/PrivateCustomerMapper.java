package car.rental.core.customers.infrastructure.mapper;

import car.rental.core.customers.domain.model.Customer;
import car.rental.core.customers.domain.model.CustomerType;
import car.rental.core.customers.dto.CreateCustomerRequest;
import car.rental.core.customers.infrastructure.persistence.CustomerProfileEntity;
import car.rental.core.customers.infrastructure.persistence.PrivateCustomerEntity;

public class PrivateCustomerMapper {
    // --- API → Domain ---
    public static Customer toDomain(CreateCustomerRequest request) {
        if (request == null) {
            return null;
        }

        return Customer.builder()
                .id(request.getUserId())
                .customerType(request.getCustomerType())
                .dateOfBirth(request.getDateOfBirth())
                .driverLicenseNo(request.getDriverLicenseNo())
                .companyName(request.getCompanyName())
                .taxNumber(request.getTaxNumber())
                .registrationNumber(request.getRegistrationNumber())
                .build();
    }


    // --- Domain → Entity ---
    public static PrivateCustomerEntity toEntity(Customer domain, CustomerProfileEntity customerProfileEntity) {
        if (domain == null || customerProfileEntity == null) {
            return null;
        }
        PrivateCustomerEntity entity = new PrivateCustomerEntity();
        entity.setId(domain.getId());
        entity.setDateOfBirth(domain.getDateOfBirth());
        entity.setDriverLicenseNo(domain.getDriverLicenseNo());
        entity.setCustomerProfile(customerProfileEntity);
        return entity;
    }

    // --- Entity → Domain ---
    public static Customer toDomain(PrivateCustomerEntity entity) {
        if (entity == null) {
            return null;
        }
        return Customer.builder()
                .id(entity.getId())
                .customerType(CustomerType.PRIVATE)
                .dateOfBirth(entity.getDateOfBirth())
                .driverLicenseNo(entity.getDriverLicenseNo())
                .companyName(null)
                .taxNumber(null)
                .registrationNumber(null)
                .build();
    }

    public static void updateEntity(PrivateCustomerEntity entity, Customer domain) {
        if (entity == null || domain == null) {
            return;
        }
        entity.setDateOfBirth(domain.getDateOfBirth());
        entity.setDriverLicenseNo(domain.getDriverLicenseNo());
        // Assuming customerProfile doesn't change, or update if needed
    }

}
