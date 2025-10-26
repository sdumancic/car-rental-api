package car.rental.core.vehicledamage.infrastructure.restclient;

import car.rental.core.vehicledamage.dto.DetectionResponse;
import car.rental.core.vehicledamage.dto.ImageUploadForm;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "car-damage-client")
public interface CarDamageClient {
    @POST
    @Path("/detect")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    DetectionResponse detectDamage(ImageUploadForm form);
}
