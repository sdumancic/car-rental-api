package car.rental.core.users.api;

import car.rental.core.users.dto.LoginUserRequest;
import car.rental.core.users.dto.RefreshTokenRequest;
import car.rental.core.users.dto.TokenResponse;
import car.rental.core.users.service.UserService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/v1/auth")
public class AuthResource {

    @Inject
    UserService userService;

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

