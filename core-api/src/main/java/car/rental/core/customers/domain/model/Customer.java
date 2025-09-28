package car.rental.core.customers.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@Builder
@ToString
public class Customer {
    private Long id;
    private CustomerType customerType;
    private Instant dateOfBirth;
    private String driverLicenseNo;
    private String companyName;
    private String taxNumber;
    private String registrationNumber;
}
