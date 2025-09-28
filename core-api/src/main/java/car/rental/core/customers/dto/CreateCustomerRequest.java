package car.rental.core.customers.dto;

import car.rental.core.customers.domain.model.CustomerType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CreateCustomerRequest {

    private Long userId;
    private CustomerType customerType;
    private Instant dateOfBirth;
    private String driverLicenseNo;
    private String companyName;
    private String taxNumber;
    private String registrationNumber;
}

