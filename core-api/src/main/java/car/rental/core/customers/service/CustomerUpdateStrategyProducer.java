package car.rental.core.customers.service;

import car.rental.core.customers.domain.model.CustomerType;
import car.rental.core.customers.domain.strategy.CustomerTypeQualifier;
import car.rental.core.customers.domain.strategy.CustomerUpdateStrategy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumMap;
import java.util.Map;

@ApplicationScoped
@Slf4j
public class CustomerUpdateStrategyProducer {

    @Inject
    @Any
    Instance<CustomerUpdateStrategy> strategies;

    @Produces
    public Map<CustomerType, CustomerUpdateStrategy> produceUpdateStrategyMap() {
        Map<CustomerType, CustomerUpdateStrategy> map = new EnumMap<>(CustomerType.class);
        for (CustomerUpdateStrategy strategy : strategies) {
            CustomerTypeQualifier qualifier = strategy.getClass().getAnnotation(CustomerTypeQualifier.class);
            if (qualifier == null) {
                // Try superclass for proxy classes
                qualifier = strategy.getClass().getSuperclass().getAnnotation(CustomerTypeQualifier.class);
            }
            if (qualifier != null) {
                map.put(qualifier.value(), strategy);
            } else {
                log.warn("No CustomerTypeQualifier found for strategy {}", strategy.getClass().getName());
            }
        }
        return map;
    }
}
