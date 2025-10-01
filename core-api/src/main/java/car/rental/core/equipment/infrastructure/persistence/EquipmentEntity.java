package car.rental.core.equipment.infrastructure.persistence;

import car.rental.core.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "equipment")
@ToString
public class EquipmentEntity extends BaseEntity {

    private String name;
    private String description;
    private Boolean active;
}
