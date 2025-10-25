package car.rental.langchain.infrastructure.restclient;

import car.rental.langchain.domain.model.CoreUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
@Slf4j
public class CoreApiClientWrapper {

    @Inject
    @RestClient
    CoreRestClient coreRestClient;

    public CoreUser getUser(Long userId) {
        log.info("Fetching data for userId: {}", userId);
        Response response = coreRestClient.getData(userId);
        return response.readEntity(CoreUser.class);
    }
}
