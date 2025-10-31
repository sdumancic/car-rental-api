package car.rental.langchain.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class InsuranceClaim {
    private Long claimId;
    private Long userId;
    private Long vehicleId;
    private DamageLevel damageLevel;
    private double confidence;
    private String damageDescription;
    private String imageUrl;
    private ClaimStatus status;
    private LocalDateTime claimDate;
    private double estimatedCost;

    public enum ClaimStatus {
        INITIATED,
        PENDING_REVIEW,
        APPROVED,
        REJECTED,
        CLOSED
    }
}

