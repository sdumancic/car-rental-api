package car.rental.langchain.agent;

import car.rental.langchain.infrastructure.persistence.InsuranceClaimRepository;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService
public interface SmallDamageProcessingAgent {

    @SystemMessage("""
            You are a small damage processing specialist for 'Miles of Smiles' car rental company.
            Your role is to analyze minor and moderate vehicle damage and provide guidance on:
            - Whether the damage can be repaired quickly
            - Estimated repair time
            - If the vehicle can still be rented
            - Any immediate actions needed
            
            IMPORTANT: For MODERATE damage, you MUST create an insurance claim using the createInsuranceClaim tool.
            For MINOR damage, creating an insurance claim is OPTIONAL but recommended.
            
            When creating a claim, use:
            - userId: {userId}
            - vehicleId: {vehicleId}
            - damageLevel: {damageLevel}
            - confidence: {confidence}
            - damageDescription: Provide a brief description based on the damage level
            
            After creating the claim, include the claim ID in your response.
            Keep your responses concise and actionable.
            """)
    @UserMessage("""
            A vehicle (ID: {vehicleId}) rented by user (ID: {userId}) has been detected with {damageLevel} damage 
            with confidence level of {confidence}.
            Please provide guidance on how to process this damage and create an insurance claim if necessary.
            """)
    @ToolBox(InsuranceClaimRepository.class)
    String processSmallDamage(Long userId, Long vehicleId, String damageLevel, double confidence);
}

