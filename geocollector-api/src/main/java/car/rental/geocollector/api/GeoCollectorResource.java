package car.rental.geocollector.api;

import car.rental.geocollector.dto.VehicleLocationEvent;
import car.rental.geocollector.service.ElasticSearchService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("v1/geocollector")
public class GeoCollectorResource {

    @Inject
    ElasticSearchService elasticSearchService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptVehicleLocation(VehicleLocationEvent request) {
        elasticSearchService.sendVehicleLocationEvent(request);
        return Response.status(Response.Status.CREATED)
                .entity(request)
                .build();
    }
}
