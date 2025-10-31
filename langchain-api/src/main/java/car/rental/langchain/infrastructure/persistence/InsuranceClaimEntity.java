package car.rental.langchain.infrastructure.persistence;

import car.rental.langchain.domain.model.DamageLevel;
import car.rental.langchain.domain.model.InsuranceClaim;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "insurance_claims")
@Getter
@Setter
@ToString
public class InsuranceClaimEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "damage_level", nullable = false)
    private DamageLevel damageLevel;

    @Column(name = "confidence")
    private double confidence;

    @Column(name = "damage_description", length = 2000)
    private String damageDescription;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InsuranceClaim.ClaimStatus status;

    @Column(name = "claim_date", nullable = false, columnDefinition = "datetime2")
    private LocalDateTime claimDate;

    @Column(name = "estimated_cost")
    private double estimatedCost;
}

