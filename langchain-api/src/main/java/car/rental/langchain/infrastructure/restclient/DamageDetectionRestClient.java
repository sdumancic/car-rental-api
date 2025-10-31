package car.rental.langchain.infrastructure.restclient;

import car.rental.langchain.domain.model.DamageDetectionResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import java.io.File;

@Path("/detect")
@RegisterRestClient(configKey = "damage-detection-api")
public interface DamageDetectionRestClient {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    DamageDetectionResponse detectDamage(@RestForm("file") @PartType(MediaType.APPLICATION_OCTET_STREAM) File file);
}

