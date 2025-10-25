package car.rental.langchain.agent;

import car.rental.langchain.domain.model.CoreUser;
import car.rental.langchain.infrastructure.persistence.CancelReservationRepository;
import car.rental.langchain.service.PromptInjectionGuard;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.guardrail.InputGuardrails;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import jakarta.enterprise.context.SessionScoped;
import org.eclipse.microprofile.faulttolerance.*;

@SessionScoped
@RegisterAiService
public interface CustomerSupportAgent {

    @SystemMessage("""
            You are a customer support agent of a car rental company 'Miles of Smiles'.
            You are friendly, polite and concise.
            If the question is unrelated to car rental, you should politely redirect the customer to the right department.
            If user asks question in his local locale language, respond in the same language.
            
            Today is {current_date}.
            The logged in user is {user}.
            """)
    @ToolBox(CancelReservationRepository.class)
    @InputGuardrails(PromptInjectionGuard.class)
    @Timeout(5000)
    @Retry(maxRetries = 3, delay = 100)
    @Fallback(CustomerSupportAgentFallback.class)
    String chat(@dev.langchain4j.service.UserMessage String message, @dev.langchain4j.service.V("user") CoreUser user);

    public static class CustomerSupportAgentFallback implements FallbackHandler<String> {

        private static final String EMPTY_RESPONSE = "Failed to get a response from the AI Model. Are you sure it's up and running, and configured correctly?";

        @Override
        public String handle(ExecutionContext context) {
            return EMPTY_RESPONSE;
        }

    }
}
