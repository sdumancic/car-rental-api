package car.rental.core.reservation.api;

import car.rental.core.common.dto.PageResponse;
import car.rental.core.reservation.domain.model.Reservation;
import car.rental.core.reservation.domain.model.ReservationStatus;
import car.rental.core.reservation.dto.*;
import car.rental.core.reservation.service.ReservationService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Instant;

@RequestScoped
@Path("/v1/reservations")
public class ReservationResource {
    @Inject
    ReservationService reservationService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReservation(@Valid CreateReservationRequest request) {
        Reservation reservation = reservationService.createReservation(request);
        return Response.status(Response.Status.CREATED)
                .entity(reservation)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findReservationById(@PathParam("id") Long id) {
        Reservation reservation = reservationService.findReservationById(id);
        if (reservation == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK)
                .entity(reservation)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findReservations(
            @QueryParam("userId") Long userId,
            @QueryParam("vehicleId") Long vehicleId,
            @QueryParam("startDate") String startDateStr,
            @QueryParam("endDate") String endDateStr,
            @QueryParam("status") String status,
            @QueryParam("sort") String sort,
            @QueryParam("notCompleted") Integer notCompleted,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("size") @DefaultValue("10") Integer size) {

        Instant startDate = startDateStr != null ? Instant.parse(startDateStr) : null;
        Instant endDate = endDateStr != null ? Instant.parse(endDateStr) : null;

        QueryReservationRequest query = QueryReservationRequest.builder()
                .userId(userId)
                .vehicleId(vehicleId)
                .startDate(startDate)
                .endDate(endDate)
                .status(status != null ? ReservationStatus.valueOf(status.toUpperCase()) : null)
                .sort(sort)
                .notCompleted(notCompleted)
                .page(page)
                .size(size)
                .build();

        PageResponse<Reservation> response = reservationService.findReservations(query);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateReservation(@PathParam("id") Long id, @Valid UpdateReservationRequest request) {
        Reservation reservation = reservationService.updateReservation(id, request);
        return Response.ok(reservation).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteReservation(@PathParam("id") Long id) {
        reservationService.deleteReservation(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/calculate-price")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculatePrice(@Valid CalculatePriceRequest request) {
        CalculatePriceResponse response = reservationService.calculateReservationPrice(request);
        return Response.ok(response).build();
    }

    @POST
    @Path("/{id}/payment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response payment(@PathParam("id") Long id, Reservation reservation) {
        Reservation updated = reservationService.completeAndSendEvent(id, reservation);
        return Response.ok(updated).build();
    }

    @POST
    @Path("/{id}/complete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response completeReservation(@PathParam("id") Long id) {
        Reservation updated = reservationService.setStatusCompleted(id);
        return Response.ok(updated).build();
    }
}
