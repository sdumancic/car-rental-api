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

    @Column(name = "company_name", nullable = false)
    private String companyName;
    @Column(name = "tax_number")
    private String taxNumber;
    @Column(name = "registration_number")
    private String registrationNumber;
}
