package car.rental.core.vehiclemedia.infrastructure.persistence;

import car.rental.core.common.domain.BaseEntity;
import car.rental.core.vehicle.infrastructure.persistence.VehicleEntity;
import car.rental.core.vehiclemedia.domain.model.VehicleMediaType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "vehicle_media")
@ToString
public class VehicleMediaEntity extends BaseEntity {

    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_type")
    private String fileType;
    private String url;
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type")
    private VehicleMediaType vehicleMediaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @ToString.Exclude
    private VehicleEntity vehicleEntity;

}
