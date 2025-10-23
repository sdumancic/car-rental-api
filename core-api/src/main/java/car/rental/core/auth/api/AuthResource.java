package car.rental.core.auth.api;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequestScoped
@Path("/v1/auth")
@Slf4j
public class AuthResource {

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    @Claim(standard = Claims.preferred_username)
    String username;

    @Inject
    @Claim(standard = Claims.groups)
    Set<String> groups;

    @GET
    @Path("/whoami")
    public String me() {
        return securityIdentity.getPrincipal().getName();
    }

    @GET
    @Path("/roles")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getRoles() {
        try {
            String currentUser = securityIdentity.getPrincipal().getName();
            Set<String> roles = securityIdentity.getRoles();

            Map<String, Object> roleInfo = new HashMap<>();
            roleInfo.put("username", currentUser);
            roleInfo.put("roles", roles);

            // Groups from JWT token (often contains realm roles)
            if (groups != null && !groups.isEmpty()) {
                roleInfo.put("groups", groups);
            }

            // Check if user is anonymous
            roleInfo.put("isAnonymous", securityIdentity.isAnonymous());

            log.info("Roles for user {}: {}", currentUser, roles);

            return Response.ok(roleInfo).build();
        } catch (Exception e) {
            log.error("Error getting roles", e);
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Unable to retrieve roles: " + e.getMessage()))
                    .build();
        }
    }

}
