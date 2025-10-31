package car.rental.langchain.service;

import car.rental.langchain.agent.LargeDamageProcessingAgent;
import car.rental.langchain.agent.SmallDamageProcessingAgent;
import car.rental.langchain.domain.model.DamageAssessment;
import car.rental.langchain.domain.model.DamageDetectionResponse;
import car.rental.langchain.domain.model.DamageLevel;
import car.rental.langchain.infrastructure.persistence.InsuranceClaimRepository;
import car.rental.langchain.infrastructure.restclient.DamageDetectionClientWrapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
@Slf4j
public class DamageAssessmentService {

    @Inject
    DamageDetectionClientWrapper damageDetectionClient;

    @Inject
    SmallDamageProcessingAgent smallDamageAgent;

    @Inject
    LargeDamageProcessingAgent largeDamageAgent;

    @Inject
    InsuranceClaimRepository insuranceClaimRepository;

    public DamageAssessment assessDamage(File imageFile, Long userId, Long vehicleId) {
        log.info("Starting damage assessment for image: {}, user: {}, vehicle: {}",
                imageFile.getName(), userId, vehicleId);

        // Step 1: Call the damage detection API
        DamageDetectionResponse detectionResponse = damageDetectionClient.detectDamage(imageFile);
        DamageLevel damageLevel = detectionResponse.getDamageLevel();
        double confidence = detectionResponse.getMaxConfidence();

        log.info("Damage detection result - Level: {}, Confidence: {}", damageLevel, confidence);

        // Step 2: Process based on damage level
        DamageAssessment assessment = DamageAssessment.builder()
                .damageLevel(damageLevel)
                .confidence(confidence)
                .build();

        switch (damageLevel) {
            case NONE:
                log.info("NO_DAMAGE detected. No further processing needed.");
                assessment.setMessage("No damage detected on the vehicle.");
                assessment.setProcessingResult("no_damage");
                break;

            case MINOR:
            case MODERATE:
                log.info("Small damage detected ({}). Calling small damage processing agent.", damageLevel);
                String smallDamageResult = processSmallDamage(userId, vehicleId, damageLevel, confidence);
                log.info("Small damage processing result: {}", smallDamageResult);
                assessment.setMessage("Small damage detected and processed.");
                assessment.setProcessingResult(smallDamageResult);

                // Extract insurance claim ID if present
                extractInsuranceClaimInfo(assessment, smallDamageResult);
                break;

            case SEVERE:
                log.info("Large damage detected ({}). Calling large damage processing agent.", damageLevel);
                String largeDamageResult = processLargeDamage(userId, vehicleId, damageLevel, confidence);
                log.info("Large damage processing result: {}", largeDamageResult);
                assessment.setMessage("Severe damage detected and processed.");
                assessment.setProcessingResult(largeDamageResult);

                // Extract insurance claim ID if present
                extractInsuranceClaimInfo(assessment, largeDamageResult);
                break;

            default:
                log.warn("Unknown damage level: {}", damageLevel);
                assessment.setMessage("Unable to determine damage level.");
                assessment.setProcessingResult("unknown");
        }

        log.info("Damage assessment completed for image: {}", imageFile.getName());
        return assessment;
    }

    private String processSmallDamage(Long userId, Long vehicleId, DamageLevel damageLevel, double confidence) {
        try {
            return smallDamageAgent.processSmallDamage(userId, vehicleId, damageLevel.name(), confidence);
        } catch (Exception e) {
            log.error("Error processing small damage with AI agent: {}", e.getMessage(), e);
            // Fallback: create insurance claim manually if it's moderate damage
            if (damageLevel == DamageLevel.MODERATE) {
                Long claimId = insuranceClaimRepository.createInsuranceClaim(
                        userId, vehicleId, damageLevel.name(), confidence,
                        "Automatic fallback claim for moderate damage"
                );
                return String.format("Small damage detected and logged. Insurance claim #%d created. " +
                        "Manual review recommended. Confidence: %.2f%%", claimId, confidence * 100);
            }
            return "Small damage detected and logged. Manual review recommended. Confidence: " + (confidence * 100) + "%";
        }
    }

    private String processLargeDamage(Long userId, Long vehicleId, DamageLevel damageLevel, double confidence) {
        try {
            return largeDamageAgent.processLargeDamage(userId, vehicleId, damageLevel.name(), confidence);
        } catch (Exception e) {
            log.error("Error processing large damage with AI agent: {}", e.getMessage(), e);
            // Fallback: create insurance claim manually for severe damage
            Long claimId = insuranceClaimRepository.createInsuranceClaim(
                    userId, vehicleId, damageLevel.name(), confidence,
                    "Automatic fallback claim for severe damage"
            );
            return String.format("Severe damage detected and logged. Insurance claim #%d created automatically. " +
                    "Immediate manual review required. Confidence: %.2f%%", claimId, confidence * 100);
        }
    }

    private void extractInsuranceClaimInfo(DamageAssessment assessment, String agentResponse) {
        // Extract insurance claim ID from agent response using regex
        // Looking for patterns like "Claim #123" or "claim ID 123" or "claim 123"
        Pattern pattern = Pattern.compile("(?:claim|Claim)\\s*(?:#|ID)?\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(agentResponse);

        if (matcher.find()) {
            Long claimId = Long.parseLong(matcher.group(1));
            assessment.setInsuranceClaimId(claimId);
            assessment.setInsuranceClaimStatus("INITIATED");
            log.info("Extracted insurance claim ID: {}", claimId);
        }
    }
}

