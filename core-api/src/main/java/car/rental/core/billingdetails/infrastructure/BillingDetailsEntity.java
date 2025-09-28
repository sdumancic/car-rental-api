package car.rental.core.billingdetails.infrastructure;

import car.rental.core.billingdetails.domain.BillingProvider;
import car.rental.core.common.domain.BaseEntity;
import car.rental.core.users.infrastructure.persistence.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.CreditCardNumber;

@Getter
@Setter
@Entity
@Table(
    name = "billing_details",
    indexes = {
        @Index(name = "idx_billing_details_user_id", columnList = "user_id")
    }
)
@NoArgsConstructor
public class BillingDetailsEntity extends BaseEntity {
    private Long id;
    @CreditCardNumber
    @NotEmpty
    @Column(name="card_number", nullable = false)
    private String cardNumber;
    @Column(name="card_holder", nullable = false)
    private String cardHolder;
    @Column(name="expiry_date", nullable = false)
    private String expiryDate;
    @Column(name="billing_address", nullable = false)
    private String billingAddress;
    @Column(name="provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private BillingProvider provider;
    @Column(name="active", nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
