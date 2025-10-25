package car.rental.langchain.service;

import car.rental.langchain.agent.CustomerSupportAgent;
import car.rental.langchain.domain.model.CoreUser;
import car.rental.langchain.infrastructure.restclient.CoreApiClientWrapper;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebSocket(path = "/customer-support-agent/{userId}")
public class CustomerSupportAgentWebSocket {

    @Inject
    CoreApiClientWrapper coreApiClientWrapper;

    private final CustomerSupportAgent customerSupportAgent;
    private CoreUser coreUser;

    public CustomerSupportAgentWebSocket(CustomerSupportAgent customerSupportAgent) {
        this.customerSupportAgent = customerSupportAgent;
    }

    @OnOpen
    public String onOpen(WebSocketConnection connection) {
        String userId = connection.pathParam("userId");
        coreUser = coreApiClientWrapper.getUser(Long.parseLong(userId));
        return "Welcome to Miles of Smiles, " + coreUser.getFirstName() + "! How can I help you today?";
    }

    @OnTextMessage
    public String onMessage(String message) {
        // Optionally, you can use userId here if needed
        return customerSupportAgent.chat(message, coreUser);
    }
}
