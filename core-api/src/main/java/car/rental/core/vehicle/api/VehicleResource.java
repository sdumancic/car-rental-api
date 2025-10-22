package car.rental.core.vehicle.api;

import car.rental.core.common.dto.PageResponse;
import car.rental.core.vehicle.domain.model.FuelType;
import car.rental.core.vehicle.domain.model.TransmissionType;
import car.rental.core.vehicle.domain.model.Vehicle;
import car.rental.core.vehicle.domain.model.VehicleType;
import car.rental.core.vehicle.dto.CreateVehicleRequest;
import car.rental.core.vehicle.dto.QueryVehicleRequest;
import car.rental.core.vehicle.dto.VehicleAvailabilityDto;
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
    public Response findVehicleById(@PathParam("id") Long id) {
        Vehicle vehicle = vehicleService.findVehicleById(id);
        return Response.status(Response.Status.OK)
                .entity(vehicle)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findVehicles(
            @QueryParam("make") String make,
            @QueryParam("model") String model,
            @QueryParam("year") Integer year,
            @QueryParam("vehicleType") String vehicleType,
            @QueryParam("passengers") Integer passengers,
            @QueryParam("doors") Integer doors,
            @QueryParam("fuelType") String fuelType,
            @QueryParam("transmission") String transmission,
            @QueryParam("sort") String sort,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("size") @DefaultValue("10") Integer size,
            @QueryParam("reservationStart") String reservationStart,
            @QueryParam("reservationEnd") String reservationEnd) {

        QueryVehicleRequest query = new QueryVehicleRequest();
        query.setMake(make);
        query.setModel(model);
        query.setYear(year);
        query.setVehicleType(vehicleType != null ? VehicleType.valueOf(vehicleType) : null);
        query.setPassengers(passengers);
        query.setDoors(doors);
        query.setFuelType(fuelType != null ? FuelType.valueOf(fuelType) : null);
        query.setTransmission(transmission != null ? TransmissionType.valueOf(transmission) : null);
        query.setSort(sort);
        query.setPage(page);
        query.setSize(size);
        query.setReservationStart(reservationStart);
        query.setReservationEnd(reservationEnd);

        // Call the new service method that returns vehicles with availability flag
        PageResponse<VehicleAvailabilityDto> response = vehicleService.findVehiclesWithAvailability(query);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateVehicle(@PathParam("id") Long id, @Valid CreateVehicleRequest request) {
        Vehicle vehicle = vehicleService.updateVehicle(id, request);
        return Response.ok(vehicle).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteVehicle(@PathParam("id") Long id) {
        vehicleService.softDeleteVehicle(id);
        return Response.noContent().build();
    }
}
