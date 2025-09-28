package car.rental.core.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email
    private String email;
    private String password;
    private String phoneNumber;
    private String mobileNumber;
    private String homeStreet;
    private String homeHouseNumber;
    private String homeZipcode;
    private String homeCity;
    private String billingStreet;
    private String billingHouseNumber;
    private String billingZipcode;
    private String billingCity;
}

