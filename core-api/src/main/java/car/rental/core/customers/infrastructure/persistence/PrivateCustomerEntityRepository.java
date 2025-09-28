package car.rental.core.customers.infrastructure.persistence;

import car.rental.core.users.infrastructure.persistence.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PrivateCustomerEntityRepository implements PanacheRepository<PrivateCustomerEntity>{

}
