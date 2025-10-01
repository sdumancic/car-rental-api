package car.rental.core.equipment.api;

import car.rental.core.equipment.domain.model.Equipment;
import car.rental.core.equipment.dto.CreateEquipmentRequest;
import car.rental.core.equipment.service.EquipmentService;
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
@Path("/v1/equipment")
public class EquipmentResource {
    @Inject
    EquipmentService equipmentService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createEquipment(@Valid CreateEquipmentRequest request) {
        Equipment equipment = equipmentService.createEquipment(request);
        return Response.status(Response.Status.CREATED)
                .entity(equipment)
                .build();
    }
}
