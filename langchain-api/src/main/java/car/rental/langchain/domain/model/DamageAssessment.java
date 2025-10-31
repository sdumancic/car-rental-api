package car.rental.langchain.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DamageAssessment {
    private DamageLevel damageLevel;
    private double confidence;
    private String message;
    private String processingResult;
    private Long insuranceClaimId;
    private String insuranceClaimStatus;
}

