package car.rental.core.customers.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomerProfileEntityRepository implements PanacheRepository<CustomerProfileEntity>{

}
