package car.rental.core.auth.api;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
@Path("/v1/auth")
@Slf4j
public class AuthResource {

    @Inject
    SecurityIdentity keycloakSecurityContext;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/whoami")
    public String me() {
        log.info(jwt.toString());
        return keycloakSecurityContext.getPrincipal().getName();
    }

}
