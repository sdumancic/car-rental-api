package car.rental.core.billingdetails.infrastructure.persistence;

import car.rental.core.billingdetails.infrastructure.BillingDetailsEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BillingDetailsEntityRepository implements PanacheRepository<BillingDetailsEntity> {

}
