package car.rental.core.customers.infrastructure.persistence;

import car.rental.core.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "private_customers")
@ToString
public class PrivateCustomerEntity extends BaseEntity {

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private CustomerProfileEntity customerProfile;

    @Column(name = "date_of_birth", columnDefinition = "datetime2")
    private Instant dateOfBirth;
    @Column(name = "driver_license_no", nullable = false, unique = true)
    private String driverLicenseNo;
}
