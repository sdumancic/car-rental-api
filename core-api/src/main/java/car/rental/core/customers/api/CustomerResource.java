package car.rental.core.customers.api;

import car.rental.core.customers.domain.model.Customer;
import car.rental.core.customers.dto.CreateCustomerRequest;
import car.rental.core.customers.service.CustomerService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@RequestScoped
@Path("/v1/customers")
@RequiredArgsConstructor
public class CustomerResource {

    private final CustomerService customerService;
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCustomer(CreateCustomerRequest request) {
        Customer customer = customerService.createCustomer(request);
        return Response.status(Response.Status.CREATED)
                .entity(customer)
                .build();
    }
}
