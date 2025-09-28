package car.rental.core.users.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Address {
    private String street;
    private String houseNumber;
    private String zipcode;
    private String city;
}
