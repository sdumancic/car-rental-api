package car.rental.core.customers.infrastructure.mapper;

import car.rental.core.customers.domain.model.Customer;
import car.rental.core.customers.domain.model.CustomerType;
import car.rental.core.customers.dto.CreateCustomerRequest;
import car.rental.core.customers.infrastructure.persistence.BusinessCustomerEntity;
import car.rental.core.customers.infrastructure.persistence.CustomerProfileEntity;
import car.rental.core.customers.infrastructure.persistence.PrivateCustomerEntity;

public class BusinessCustomerMapper {
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
    public static BusinessCustomerEntity toEntity(Customer domain, CustomerProfileEntity customerProfileEntity) {
        if (domain == null) {
            return null;
        }
        BusinessCustomerEntity entity = new BusinessCustomerEntity();
        entity.setId(domain.getId());
        entity.setCompanyName(domain.getCompanyName());
        entity.setTaxNumber(domain.getTaxNumber());
        entity.setRegistrationNumber(domain.getRegistrationNumber());
        entity.setCustomerProfile(customerProfileEntity);
        return entity;
    }

    // --- Entity → Domain ---
    public static Customer toDomain(BusinessCustomerEntity entity) {
        if (entity == null) {
            return null;
        }
        return Customer.builder()
                .id(entity.getId())
                .customerType(CustomerType.BUSINESS)
                .dateOfBirth(null)
                .driverLicenseNo(null)
                .companyName(entity.getCompanyName())
                .taxNumber(entity.getTaxNumber())
                .registrationNumber(entity.getRegistrationNumber())
                .build();

    }

}
