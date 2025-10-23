package car.rental.core.users.api;

import car.rental.core.users.dto.LoginUserRequest;
import car.rental.core.users.dto.RefreshTokenRequest;
import car.rental.core.users.dto.TokenResponse;
import car.rental.core.users.service.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequestScoped
@Path("/v1/auth")
public class AuthResource {

    @Inject
    SecurityIdentity securityIdentity;

    @Inject
    UserService userService;

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

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid LoginUserRequest request) {
        try {
            TokenResponse tokens = userService.login(request);
            return Response.ok(tokens).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Login failed: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/refresh_token")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response refresh(@Valid RefreshTokenRequest request) {
        try {
            TokenResponse tokens = userService.refresh(request.getUsername());
            return Response.ok(tokens).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Refresh failed: " + e.getMessage())
                    .build();
        }
    }

}

