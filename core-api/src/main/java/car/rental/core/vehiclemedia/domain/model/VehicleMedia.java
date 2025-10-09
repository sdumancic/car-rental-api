package car.rental.core.vehiclemedia.domain.model;

import car.rental.core.vehicle.domain.model.Vehicle;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class VehicleMedia {
    private Long id;
    private VehicleMediaType vehicleMediaType;
    private String fileName;
    private String fileType;
    private String url;
    private Vehicle vehicle;
}
