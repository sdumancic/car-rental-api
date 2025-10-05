package car.rental.core.vehicle.api;

import car.rental.core.vehicle.domain.model.Vehicle;
import car.rental.core.vehicle.dto.CreateVehicleRequest;
import car.rental.core.vehicle.service.VehicleService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
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

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findVehicleById(Long id) {
        Vehicle vehicle = vehicleService.findVehicleById(id);
        return Response.status(Response.Status.OK)
                .entity(vehicle)
                .build();
    }
}
