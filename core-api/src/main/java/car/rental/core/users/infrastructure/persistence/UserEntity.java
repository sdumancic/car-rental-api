package car.rental.core.users.infrastructure.persistence;

import car.rental.core.billingdetails.infrastructure.BillingDetailsEntity;
import car.rental.core.common.domain.BaseEntity;
import car.rental.core.customers.infrastructure.persistence.CustomerProfileEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_username_unique", columnList = "username", unique = true),
        @Index(name = "idx_users_email_unique", columnList = "email", unique = true)
})
@NoArgsConstructor
public class UserEntity extends BaseEntity {
    @NotBlank
    @Size(min = 5, max = 18, message = "Name is required, maximum 18 characters.")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "first_name")
    private String firstName;

    @NotBlank
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "home_street")),
            @AttributeOverride(name = "houseNumber", column = @Column(name = "home_house_number")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "home_zipcode")),
            @AttributeOverride(name = "city", column = @Column(name = "home_city"))
    })
    private AddressEmbeddable homeAddress;

    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "billing_street")),
            @AttributeOverride(name = "houseNumber", column = @Column(name = "billing_house_number")),
            @AttributeOverride(name = "zipcode", column = @Column(name = "billing_zipcode")),
            @AttributeOverride(name = "city", column = @Column(name = "billing_city"))
    })
    private AddressEmbeddable billingAddress;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("active DESC, dateCreated DESC")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<BillingDetailsEntity> billingDetails = new java.util.HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    protected CustomerProfileEntity customerProfile;

    @Column(name = "driver_license_blob_id")
    private String driverLicenseBlobId;
    @Column(name = "keycloak_id")
    private String keycloakId;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}

