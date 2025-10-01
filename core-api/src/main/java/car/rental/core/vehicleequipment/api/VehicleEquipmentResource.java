package car.rental.core.vehicleequipment.api;

import car.rental.core.vehicleequipment.domain.model.VehicleEquipment;
import car.rental.core.vehicleequipment.dto.CreateVehicleEquipmentRequest;
import car.rental.core.vehicleequipment.service.VehicleEquipmentService;
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
}
