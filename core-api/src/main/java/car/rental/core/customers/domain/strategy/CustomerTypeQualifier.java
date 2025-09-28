package car.rental.core.customers.domain.strategy;

import car.rental.core.customers.domain.model.CustomerType;
import jakarta.inject.Qualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomerTypeQualifier {
    CustomerType value();
}

