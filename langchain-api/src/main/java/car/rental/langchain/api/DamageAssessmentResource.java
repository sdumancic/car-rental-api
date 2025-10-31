package car.rental.langchain.api;

import car.rental.langchain.domain.model.DamageAssessment;
import car.rental.langchain.service.DamageAssessmentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Path("/api/damage-assessment")
@Slf4j
public class DamageAssessmentResource {

    @Inject
    DamageAssessmentService damageAssessmentService;

    @POST
    @Path("/assess")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response assessDamage(
            @RestForm("file") @PartType(MediaType.APPLICATION_OCTET_STREAM) FileUpload fileUpload,
            @RestForm("userId") Long userId,
            @RestForm("vehicleId") Long vehicleId) {

        if (fileUpload == null || fileUpload.filePath() == null) {
            log.error("No file uploaded");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"No file uploaded\"}")
                    .build();
        }

        if (userId == null) {
            log.error("User ID is required");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"User ID is required\"}")
                    .build();
        }

        if (vehicleId == null) {
            log.error("Vehicle ID is required");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Vehicle ID is required\"}")
                    .build();
        }

        File tempFile = null;
        try {
            // Create a temporary file
            tempFile = createTempFile(fileUpload);
            log.info("Processing uploaded image: {} for user: {} and vehicle: {}",
                    fileUpload.fileName(), userId, vehicleId);

            // Assess the damage
            DamageAssessment assessment = damageAssessmentService.assessDamage(tempFile, userId, vehicleId);

            log.info("Damage assessment completed successfully for file: {}", fileUpload.fileName());
            return Response.ok(assessment).build();

        } catch (IOException e) {
            log.error("Error handling uploaded file: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error processing uploaded file\"}")
                    .build();
        } catch (Exception e) {
            log.error("Error assessing damage: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error assessing damage: " + e.getMessage() + "\"}")
                    .build();
        } finally {
            // Clean up temporary file
            if (tempFile != null && tempFile.exists()) {
                try {
                    Files.delete(tempFile.toPath());
                    log.debug("Temporary file deleted: {}", tempFile.getAbsolutePath());
                } catch (IOException e) {
                    log.warn("Failed to delete temporary file: {}", tempFile.getAbsolutePath(), e);
                }
            }
        }
    }

    private File createTempFile(FileUpload fileUpload) throws IOException {
        String originalFilename = fileUpload.fileName();
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }

        java.nio.file.Path tempFile = Files.createTempFile("damage-assessment-", extension);
        Files.copy(fileUpload.filePath(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        return tempFile.toFile();
    }
}

