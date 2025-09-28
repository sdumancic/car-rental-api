package car.rental.core.users.api;

import car.rental.core.users.domain.model.User;
import car.rental.core.users.dto.CreateUserRequest;
import car.rental.core.users.service.UserService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/v1/users")
public class UserResource {
    @Inject
    UserService userService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(CreateUserRequest request) {
        User user = userService.createUser(request);
        return Response.status(Response.Status.CREATED)
                .entity(user)
                .build();
    }
}
