package car.rental.core.billingdetails.domain.repository;

import car.rental.core.billingdetails.domain.model.BillingDetails;
import car.rental.core.common.domain.BaseRepository;

import java.util.List;

public interface BillingDetailsRepository extends BaseRepository<BillingDetails> {
    List<BillingDetails> findByUserId(Long userId);

    BillingDetails update(BillingDetails billingDetails);

    void softDeleteById(Long id);
}
