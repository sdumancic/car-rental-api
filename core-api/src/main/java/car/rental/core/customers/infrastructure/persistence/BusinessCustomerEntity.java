package car.rental.core.customers.infrastructure.persistence;

import car.rental.core.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "business_customers")
public class BusinessCustomerEntity extends BaseEntity {

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private CustomerProfileEntity customerProfile;

    private String companyName;
    private String taxNumber;
    private String registrationNumber;
}
