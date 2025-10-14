package car.rental.core.pricing.api;

import car.rental.core.pricing.domain.model.Pricing;
import car.rental.core.pricing.dto.CreatePricingRequest;
import car.rental.core.pricing.service.PricingService;
import car.rental.core.vehicle.service.VehicleService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;

@RequestScoped
@Path("/v1/vehicles/{vehicleId}/pricing")
public class PricingResource {

    @Inject
    PricingService pricingService;

    @Inject
    VehicleService vehicleService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPricing(@PathParam("vehicleId") Long vehicleId, @Valid CreatePricingRequest request) {
        if (!vehicleId.equals(request.getVehicleId())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Vehicle ID mismatch").build();
        }
        Pricing pricing = pricingService.createPricing(request);
        return Response.status(Response.Status.CREATED)
                .entity(pricing)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPricingByVehicleId(@PathParam("vehicleId") Long vehicleId) {
        if (vehicleService.findVehicleById(vehicleId) == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Vehicle not found").build();
        }
        List<Pricing> pricingList = pricingService.findPricingByVehicleId(vehicleId);
        return Response.status(Response.Status.OK)
                .entity(pricingList)
                .build();
    }

    @GET
    @Path("/active")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findActivePricingByVehicleId(@PathParam("vehicleId") Long vehicleId) {
        if (vehicleService.findVehicleById(vehicleId) == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Vehicle not found").build();
        }
        Pricing pricing = pricingService.findActivePricingByVehicleId(vehicleId);
        if (pricing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK)
                .entity(pricing)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPricingById(@PathParam("vehicleId") Long vehicleId, @PathParam("id") Long id) {
        Pricing pricing = pricingService.findPricingById(id);
        if (pricing == null || !pricing.getVehicle().getId().equals(vehicleId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK)
                .entity(pricing)
                .build();
    }

    @GET
    @Path("/calculate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculatePrice(@PathParam("vehicleId") Long vehicleId, @QueryParam("days") @DefaultValue("1") int days) {
        if (vehicleService.findVehicleById(vehicleId) == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Vehicle not found").build();
        }
        BigDecimal price = pricingService.calculatePrice(vehicleId, days);
        if (price == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("No pricing found for the given days").build();
        }
        return Response.status(Response.Status.OK)
                .entity(price)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePricing(@PathParam("vehicleId") Long vehicleId, @PathParam("id") Long id, @Valid CreatePricingRequest request) {
        request.setVehicleId(vehicleId);
        Pricing pricing = pricingService.updatePricing(id, request);
        return Response.ok(pricing).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePricing(@PathParam("vehicleId") Long vehicleId, @PathParam("id") Long id) {
        Pricing existing = pricingService.findPricingById(id);
        if (existing == null || !existing.getVehicle().getId().equals(vehicleId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        pricingService.deletePricingById(id);
        return Response.noContent().build();
    }
}
