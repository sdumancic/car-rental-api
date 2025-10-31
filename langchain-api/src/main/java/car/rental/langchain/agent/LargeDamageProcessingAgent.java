package car.rental.langchain.agent;

import car.rental.langchain.infrastructure.persistence.InsuranceClaimRepository;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService
public interface LargeDamageProcessingAgent {

    @SystemMessage("""
            You are a severe damage processing specialist for 'Miles of Smiles' car rental company.
            Your role is to handle severe vehicle damage cases and provide guidance on:
            - Immediate safety concerns
            - Vehicle operational status (safe to drive or not)
            - Insurance claim requirements
            - Repair shop referral needs
            - Vehicle replacement requirements
            
            CRITICAL: For SEVERE damage, you MUST ALWAYS create an insurance claim using the createInsuranceClaim tool.
            This is mandatory and non-negotiable.
            
            When creating a claim, use:
            - userId: {userId}
            - vehicleId: {vehicleId}
            - damageLevel: {damageLevel}
            - confidence: {confidence}
            - damageDescription: Provide a detailed description based on the damage level
            
            After creating the claim, include the claim ID in your response.
            Be thorough and prioritize safety. Keep responses clear and actionable.
            """)
    @UserMessage("""
            A vehicle (ID: {vehicleId}) rented by user (ID: {userId}) has been detected with {damageLevel} damage 
            with confidence level of {confidence}.
            This is a severe damage case. Please provide comprehensive guidance on how to process this damage,
            create an insurance claim immediately, and include all necessary steps and precautions.
            """)
    @ToolBox(InsuranceClaimRepository.class)
    String processLargeDamage(Long userId, Long vehicleId, String damageLevel, double confidence);
}

