package car.rental.core.vehicle.infrastructure.persistence;

import car.rental.core.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "make_model")
@Getter
@Setter
@ToString
public class MakeModelEntity extends BaseEntity {

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;
}

