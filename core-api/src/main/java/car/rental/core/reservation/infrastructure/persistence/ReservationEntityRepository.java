package car.rental.core.reservation.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReservationEntityRepository implements PanacheRepository<ReservationEntity> {
}
