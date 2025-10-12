package car.rental.core.billingdetails.service;

import car.rental.core.billingdetails.domain.model.BillingDetails;
import car.rental.core.billingdetails.dto.CreateBillingDetailsRequest;
import car.rental.core.billingdetails.infrastructure.mapper.BillingDetailsMapper;
import car.rental.core.billingdetails.infrastructure.persistence.PanacheBillingDetailsRepository;
import car.rental.core.users.domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class BillingDetailsService {

    private final PanacheBillingDetailsRepository panacheBillingDetailsRepository;

    @Transactional
    public BillingDetails createBillingDetailsForUser(CreateBillingDetailsRequest request, User user) {
        BillingDetails billingDetails = BillingDetailsMapper.toDomain(request, user);
        return panacheBillingDetailsRepository.save(billingDetails);
    }

    public BillingDetails findBillingDetailsById(Long id) {
        return panacheBillingDetailsRepository.findById(id).orElse(null);
    }

    public List<BillingDetails> findBillingDetailsByUserId(Long userId) {
        return panacheBillingDetailsRepository.findByUserId(userId);
    }

    @Transactional
    public BillingDetails updateBillingDetails(BillingDetails billingDetails) {
        return panacheBillingDetailsRepository.update(billingDetails);
    }

    @Transactional
    public void deleteBillingDetailsById(Long id) {
        panacheBillingDetailsRepository.softDeleteById(id);
    }
}
