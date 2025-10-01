package car.rental.core.vehicleequipment.infrastructure.persistence;

import car.rental.core.equipment.infrastructure.persistence.EquipmentEntity;
import car.rental.core.vehicle.infrastructure.persistence.VehicleEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "vehicle_equipment",
        indexes = {
                @Index(name = "idx_vehicle_equipment_ui1", columnList = "vehicle_id,equipment_id", unique = true)
        })
@ToString
public class VehicleEquipmentEntity {

    @EmbeddedId
    private VehicleEquipmentId id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", insertable = false, updatable = false)
    private VehicleEntity vehicle;

    @ManyToOne
    @JoinColumn(name = "equipment_id", insertable = false, updatable = false)
    private EquipmentEntity equipment;
    private Boolean active;

    @NotNull
    @Column(name = "date_created", columnDefinition = "datetime2")
    private Instant dateCreated;

    @NotNull
    @Column(name = "date_modified", columnDefinition = "datetime2")
    protected Instant dateModified;
}
