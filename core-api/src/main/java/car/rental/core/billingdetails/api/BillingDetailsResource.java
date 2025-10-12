package car.rental.core.billingdetails.api;

import car.rental.core.billingdetails.domain.model.BillingDetails;
import car.rental.core.billingdetails.dto.CreateBillingDetailsRequest;
import car.rental.core.billingdetails.service.BillingDetailsService;
import car.rental.core.users.domain.model.User;
import car.rental.core.users.service.UserService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@RequestScoped
@Path("/v1/users/{userId}/billing-details")
public class BillingDetailsResource {

    @Inject
    BillingDetailsService billingDetailsService;

    @Inject
    UserService userService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBillingDetails(@PathParam("userId") Long userId, @Valid CreateBillingDetailsRequest request) {
        User user = userService.findUserById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
        BillingDetails billingDetails = billingDetailsService.createBillingDetailsForUser(request, user);
        return Response.status(Response.Status.CREATED)
                .entity(billingDetails)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findBillingDetailsByUserId(@PathParam("userId") Long userId) {
        List<BillingDetails> billingDetails = billingDetailsService.findBillingDetailsByUserId(userId);
        return Response.status(Response.Status.OK)
                .entity(billingDetails)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findBillingDetailsById(@PathParam("userId") Long userId, @PathParam("id") Long id) {
        BillingDetails billingDetails = billingDetailsService.findBillingDetailsById(id);
        if (billingDetails == null || !billingDetails.getUser().getId().equals(userId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK)
                .entity(billingDetails)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBillingDetails(@PathParam("userId") Long userId, @PathParam("id") Long id, @Valid CreateBillingDetailsRequest request) {
        BillingDetails existing = billingDetailsService.updateBillingDetails(id, request);
        return Response.ok(existing).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBillingDetails(@PathParam("userId") Long userId, @PathParam("id") Long id) {
        BillingDetails existing = billingDetailsService.findBillingDetailsById(id);
        if (existing == null || !existing.getUser().getId().equals(userId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        billingDetailsService.deleteBillingDetailsById(id);
        return Response.noContent().build();
    }
}
