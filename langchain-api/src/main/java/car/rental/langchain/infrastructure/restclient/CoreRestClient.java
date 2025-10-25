package car.rental.langchain.infrastructure.restclient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "core-api")
public interface CoreRestClient {
    @GET
    @Path("/v1/users/{userId}")
    Response getData(@PathParam("userId") Long userId);
}
