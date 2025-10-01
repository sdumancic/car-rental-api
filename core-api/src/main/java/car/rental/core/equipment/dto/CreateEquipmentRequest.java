package car.rental.core.equipment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEquipmentRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean active;
}

