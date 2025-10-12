package car.rental.core.billingdetails.dto;

import car.rental.core.billingdetails.domain.BillingProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBillingDetailsRequest {
    @NotBlank
    private String cardNumber;
    @NotBlank
    private String cardHolder;
    @NotBlank
    private String expiryDate;
    @NotBlank
    private String billingAddress;
    @NotNull
    private BillingProvider provider;
}
