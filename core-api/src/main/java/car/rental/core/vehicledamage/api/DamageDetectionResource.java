package car.rental.core.vehicledamage.api;

import car.rental.core.vehicledamage.dto.DetectionResponse;
import car.rental.core.vehicledamage.dto.ImageUploadForm;
import car.rental.core.vehicledamage.infrastructure.restclient.CarDamageClient;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/v1/vehicle-damage")
@RequestScoped
public class DamageDetectionResource {

    @Inject
    @RestClient
    CarDamageClient carDamageClient;

    @POST
    @Path("/detect")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public DetectionResponse detect(ImageUploadForm form) {
        return carDamageClient.detectDamage(form);
    }
}
