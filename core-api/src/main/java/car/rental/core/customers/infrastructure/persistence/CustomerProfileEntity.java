package car.rental.core.customers.infrastructure.persistence;

import car.rental.core.common.domain.BaseEntity;
import car.rental.core.customers.domain.model.CustomerType;
import car.rental.core.users.infrastructure.persistence.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "customer_profile")
public class CustomerProfileEntity extends BaseEntity {

    @OneToOne
    @MapsId   // tells Hibernate to use the same PK value as UserEntity
    @JoinColumn(name = "id") // foreign key column pointing to users.id
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    @OneToOne(mappedBy = "customerProfile", cascade = CascadeType.ALL, optional = true)
    private PrivateCustomerEntity privateCustomer;

    @OneToOne(mappedBy = "customerProfile", cascade = CascadeType.ALL, optional = true)
    private BusinessCustomerEntity businessCustomer;
}
