package car.rental.core.billingdetails.domain.model;

import car.rental.core.billingdetails.domain.BillingProvider;
import car.rental.core.users.domain.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

/**
 * Domain model for BillingDetails.
 * This is framework-agnostic and should not contain JPA annotations.
 */
@Getter
@Setter
@Builder
@ToString
public class BillingDetails {
    private Long id;
    private String cardNumber;
    private String cardHolder;
    private String expiryDate;
    private String billingAddress;
    private BillingProvider provider;
    private Boolean active;
    private User user;
    private Instant dateCreated;
    private Instant dateModified;
}
