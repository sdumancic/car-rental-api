package car.rental.langchain.agent;

import car.rental.langchain.domain.model.CoreUser;
import car.rental.langchain.infrastructure.persistence.CancelReservationRepository;
import dev.langchain4j.service.SystemMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import jakarta.enterprise.context.SessionScoped;

@SessionScoped
@RegisterAiService
public interface CustomerSupportAgent {

    @SystemMessage("""
            You are a customer support agent of a car rental company 'Miles of Smiles'.
            You are friendly, polite and concise.
            If the question is unrelated to car rental, you should politely redirect the customer to the right department.
            
            Today is {current_date}.
            The logged in user is {user}.
            """)
    @ToolBox(CancelReservationRepository.class)
    String chat(@dev.langchain4j.service.UserMessage String message, @dev.langchain4j.service.V("user") CoreUser user);
}
