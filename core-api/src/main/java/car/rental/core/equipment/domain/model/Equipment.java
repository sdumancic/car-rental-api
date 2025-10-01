package car.rental.core.equipment.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class Equipment {
    private Long id;
    private String name;
    private String description;
    private Boolean active;
}
