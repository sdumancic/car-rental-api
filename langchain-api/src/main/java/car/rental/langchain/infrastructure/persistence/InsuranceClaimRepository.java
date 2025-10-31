package car.rental.langchain.infrastructure.persistence;

import car.rental.langchain.domain.model.DamageLevel;
import car.rental.langchain.domain.model.InsuranceClaim;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class InsuranceClaimRepository {

    @Inject
    EntityManager entityManager;

    @Tool("Create an insurance claim for vehicle damage. Use this when damage is detected (MINOR, MODERATE, or SEVERE). Returns the claim ID.")
    @Transactional
    public Long createInsuranceClaim(Long userId, Long vehicleId, String damageLevel,
                                     double confidence, String damageDescription) {
        log.info("Creating insurance claim for user: {}, vehicle: {}, damageLevel: {}",
                userId, vehicleId, damageLevel);

        InsuranceClaimEntity claim = new InsuranceClaimEntity();
        claim.setUserId(userId);
        claim.setVehicleId(vehicleId);
        claim.setDamageLevel(DamageLevel.valueOf(damageLevel.toUpperCase()));
        claim.setConfidence(confidence);
        claim.setDamageDescription(damageDescription);
        claim.setStatus(InsuranceClaim.ClaimStatus.INITIATED);
        claim.setClaimDate(LocalDateTime.now());

        // Estimate cost based on damage level
        claim.setEstimatedCost(estimateCostByDamageLevel(damageLevel));

        entityManager.persist(claim);
        entityManager.flush();

        log.info("Insurance claim created successfully with ID: {}", claim.getId());
        return claim.getId();
    }

    @Tool("Get insurance claim details by claim ID")
    public String getInsuranceClaimById(Long claimId) {
        log.info("Fetching insurance claim with ID: {}", claimId);

        InsuranceClaimEntity claim = entityManager.find(InsuranceClaimEntity.class, claimId);
        if (claim == null) {
            return "Insurance claim not found with ID: " + claimId;
        }

        return String.format(
                "Claim ID: %d, User: %d, Vehicle: %d, Damage: %s (%.2f%% confidence), Status: %s, Estimated Cost: $%.2f, Date: %s",
                claim.getId(), claim.getUserId(), claim.getVehicleId(),
                claim.getDamageLevel(), claim.getConfidence() * 100,
                claim.getStatus(), claim.getEstimatedCost(), claim.getClaimDate()
        );
    }

    @Tool("Get all insurance claims for a specific user")
    public String getInsuranceClaimsByUserId(Long userId) {
        log.info("Fetching insurance claims for user: {}", userId);

        List<InsuranceClaimEntity> claims = entityManager
                .createQuery("SELECT c FROM InsuranceClaimEntity c WHERE c.userId = :userId", InsuranceClaimEntity.class)
                .setParameter("userId", userId)
                .getResultList();

        if (claims.isEmpty()) {
            return "No insurance claims found for user ID: " + userId;
        }

        return claims.stream()
                .map(claim -> String.format(
                        "Claim #%d: %s damage (%.1f%% confidence), Status: %s, Cost: $%.2f, Date: %s",
                        claim.getId(), claim.getDamageLevel(), claim.getConfidence() * 100,
                        claim.getStatus(), claim.getEstimatedCost(), claim.getClaimDate()
                ))
                .collect(Collectors.joining("\n"));
    }

    @Tool("Update insurance claim status. Valid statuses: INITIATED, PENDING_REVIEW, APPROVED, REJECTED, CLOSED")
    @Transactional
    public String updateInsuranceClaimStatus(Long claimId, String newStatus) {
        log.info("Updating insurance claim {} to status: {}", claimId, newStatus);

        InsuranceClaimEntity claim = entityManager.find(InsuranceClaimEntity.class, claimId);
        if (claim == null) {
            return "Error: Insurance claim not found with ID: " + claimId;
        }

        try {
            InsuranceClaim.ClaimStatus status = InsuranceClaim.ClaimStatus.valueOf(newStatus.toUpperCase());
            claim.setStatus(status);
            entityManager.merge(claim);

            log.info("Insurance claim {} status updated to {}", claimId, status);
            return String.format("Insurance claim #%d status updated to %s", claimId, status);
        } catch (IllegalArgumentException e) {
            return "Error: Invalid status. Valid statuses are: INITIATED, PENDING_REVIEW, APPROVED, REJECTED, CLOSED";
        }
    }

    private double estimateCostByDamageLevel(String damageLevel) {
        return switch (damageLevel.toUpperCase()) {
            case "MINOR" -> 500.0;
            case "MODERATE" -> 2000.0;
            case "SEVERE" -> 5000.0;
            default -> 0.0;
        };
    }
}


