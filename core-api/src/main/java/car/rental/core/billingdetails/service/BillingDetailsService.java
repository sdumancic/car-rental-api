package car.rental.core.billingdetails.service;

import car.rental.core.billingdetails.domain.model.BillingDetails;
import car.rental.core.billingdetails.domain.repository.BillingDetailsRepository;
import car.rental.core.billingdetails.dto.CreateBillingDetailsRequest;
import car.rental.core.billingdetails.infrastructure.mapper.BillingDetailsMapper;
import car.rental.core.billingdetails.infrastructure.persistence.PanacheBillingDetailsRepository;
import car.rental.core.users.domain.model.User;
import car.rental.core.users.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class BillingDetailsService {

    private final BillingDetailsRepository billingDetailsRepository;
    private final PanacheBillingDetailsRepository panacheBillingDetailsRepository;
    private final UserService userService;

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

    public BillingDetails updateBillingDetails(Long id, CreateBillingDetailsRequest request) {
        User user = userService.findUserById(id);
        BillingDetails billingDetails1 = BillingDetailsMapper.toDomain(request, user);
        billingDetails1.setId(id);
        return billingDetailsRepository.update(billingDetails1);
    }

    @Transactional
    public void deleteBillingDetailsById(Long id) {
        panacheBillingDetailsRepository.softDeleteById(id);
    }
}
