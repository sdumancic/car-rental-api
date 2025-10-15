package car.rental.core.equipment.api;

import car.rental.core.equipment.domain.model.Equipment;
import car.rental.core.equipment.dto.CreateEquipmentRequest;
import car.rental.core.equipment.service.EquipmentService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

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

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEquipment(@PathParam("id") Long id) {
        Equipment equipment = equipmentService.findEquipmentById(id);
        return Response.ok(equipment).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllEquipment() {
        List<Equipment> equipment = equipmentService.findAll();
        return Response.ok(equipment).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEquipment(@PathParam("id") Long id, @Valid CreateEquipmentRequest request) {
        Equipment equipment = equipmentService.updateEquipment(id, request);
        return Response.ok(equipment).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteEquipment(@PathParam("id") Long id) {
        equipmentService.softDeleteEquipment(id);
        return Response.noContent().build();
    }
}
