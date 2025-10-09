package car.rental.core.vehiclemedia.dto;

import car.rental.core.vehiclemedia.domain.model.VehicleMediaType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVehicleMediaRequest {
    @NotNull
    private Long vehicleId;
    @NotNull
    private String fileName;
    @NotNull
    private String fileType;
    @NotNull
    private VehicleMediaType vehicleMediaType;
}

