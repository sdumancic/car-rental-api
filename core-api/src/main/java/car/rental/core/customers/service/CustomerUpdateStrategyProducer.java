package car.rental.core.customers.service;

import car.rental.core.customers.domain.model.CustomerType;
import car.rental.core.customers.domain.strategy.CustomerTypeQualifier;
import car.rental.core.customers.domain.strategy.CustomerUpdateStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import java.util.EnumMap;
import java.util.Map;

@ApplicationScoped
public class CustomerUpdateStrategyProducer {

    @Inject
    Instance<CustomerUpdateStrategy> strategies;

    @Produces
    public Map<CustomerType, CustomerUpdateStrategy> produceUpdateStrategyMap() {
        Map<CustomerType, CustomerUpdateStrategy> map = new EnumMap<>(CustomerType.class);
        for (CustomerUpdateStrategy strategy : strategies) {
            CustomerTypeQualifier qualifier = strategy.getClass().getAnnotation(CustomerTypeQualifier.class);
            if (qualifier != null) {
                map.put(qualifier.value(), strategy);
            }
        }
        return map;
    }
}
