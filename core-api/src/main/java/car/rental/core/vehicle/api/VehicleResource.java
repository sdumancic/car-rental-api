package car.rental.core.vehicle.api;

import car.rental.core.vehicle.domain.model.Vehicle;
import car.rental.core.vehicle.dto.CreateVehicleRequest;
import car.rental.core.vehicle.service.VehicleService;
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
@Path("/v1/vehicles")
public class VehicleResource {
    @Inject
    VehicleService vehicleService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createVehicle(@Valid CreateVehicleRequest request) {
        Vehicle vehicle = vehicleService.createVehicle(request);
        return Response.status(Response.Status.CREATED)
                .entity(vehicle)
                .build();
    }
}
