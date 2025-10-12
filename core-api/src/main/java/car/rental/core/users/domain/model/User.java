package car.rental.core.users.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Domain model for a User.
 * This is framework-agnostic and should not contain JPA annotations.
 */
@Getter
@Setter
@Builder
public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String mobileNumber;
    private Instant dateCreated;
    private Instant dateModified;
    private Address homeAddress;
    private Address billingAddress;
    private String driverLicenseBlobId;
    private Boolean active;
}
