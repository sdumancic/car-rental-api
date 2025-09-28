package car.rental.core.users.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserEntityRepository implements PanacheRepository<UserEntity>{

}
