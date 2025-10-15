package car.rental.core.vehicleequipment.api;

import car.rental.core.equipment.domain.model.Equipment;
import car.rental.core.vehicleequipment.domain.model.VehicleEquipment;
import car.rental.core.vehicleequipment.dto.CreateVehicleEquipmentRequest;
import car.rental.core.vehicleequipment.service.VehicleEquipmentService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@RequestScoped
@Path("/v1/vehicle-equipment")
public class VehicleEquipmentResource {
    @Inject
    VehicleEquipmentService vehicleEquipmentService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createEquipment(@Valid CreateVehicleEquipmentRequest request) {
        VehicleEquipment vehicle = vehicleEquipmentService.createVehicleEquipment(request);
        return Response.status(Response.Status.CREATED)
                .entity(vehicle)
                .build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findEquipmentForVehicle(@QueryParam("vehicleId") Long vehicleId) {
        List<Equipment> list = vehicleEquipmentService.findEquipment(vehicleId);
        return Response.status(Response.Status.CREATED)
                .entity(list)
                .build();
    }

    @DELETE
    @Path("/{vehicleId}/{equipmentId}")
    public Response deleteEquipment(@PathParam("vehicleId") Long vehicleId, @PathParam("equipmentId") Long equipmentId) {
        vehicleEquipmentService.deleteVehicleEquipment(vehicleId, equipmentId);
        return Response.status(Response.Status.NO_CONTENT)
                .build();
    }
}
