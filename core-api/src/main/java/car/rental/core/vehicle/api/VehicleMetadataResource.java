package car.rental.core.vehicle.api;

import car.rental.core.vehicle.service.VehicleMetadataService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/v1/metadata")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VehicleMetadataResource {
    @Inject
    VehicleMetadataService vehicleMetadataService;

    @GET
    @Path("/makes")
    public Response getMakes() {
        List<String> makes = vehicleMetadataService.getAllMakes();
        return Response.ok(makes).build();
    }

    @GET
    @Path("/models")
    public Response getModels(@QueryParam("make") String make) {
        if (make == null || make.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Query param 'make' is required").build();
        }
        List<String> models = vehicleMetadataService.getModelsByMake(make);
        return Response.ok(models).build();
    }

    @GET
    @Path("/vehicle-types")
    public Response getVehicleTypes() {
        return Response.ok(vehicleMetadataService.getAllVehicleTypes()).build();
    }

    @GET
    @Path("/transmission-types")
    public Response getTransmissionTypes() {
        return Response.ok(vehicleMetadataService.getAllTransmissionTypes()).build();
    }

    @GET
    @Path("/fuel-types")
    public Response getFuelTypes() {
        return Response.ok(vehicleMetadataService.getAllFuelTypes()).build();
    }

    @GET
    @Path("/vehicle-statuses")
    public Response getVehicleStatuses() {
        return Response.ok(vehicleMetadataService.getAllVehicleStatuses()).build();
    }
}

