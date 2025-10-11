package car.rental.core.azure.api;

import car.rental.core.azure.dto.FileUploadForm;
import car.rental.core.azure.dto.UploadResult;
import car.rental.core.azure.service.AzureBlobService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import java.util.Map;

@Path("/v1/videos")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
@AllArgsConstructor
public class VideoUploadResource {

    private final AzureBlobService azureBlobService;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile(@MultipartForm FileUploadForm form,
                               @QueryParam("vehicleId") Long vehicleId) {
        try {
            UploadResult result = azureBlobService.uploadMedia(form.fileInput, form.fileName, vehicleId);
            return Response.ok(Map.of(
                    "container", result.getContainer(),
                    "blobName", result.getBlobName(),
                    "url", result.getUrl(),
                    "mediaCategory", result.getMediaCategory(),
                    "originalFileName", result.getOriginalFileName()
            )).build();
        } catch (Exception e) {
            log.error("Upload failed", e);
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/upload")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBlob(@QueryParam("blobName") String blobName,
                               @QueryParam("mediaCategory") String mediaCategory,
                               @QueryParam("vehicleId") Long vehicleId) {
        try {
            boolean deleted = azureBlobService.deleteBlob(blobName, vehicleId);
            if (deleted) {
                return Response.ok(Map.of("deleted", true)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("deleted", false, "message", "Blob or container not found"))
                        .build();
            }
        } catch (Exception e) {
            log.error("Delete failed", e);
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }

    @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadBlob(@QueryParam("blobName") String blobName,
                                 @QueryParam("vehicleId") Long vehicleId) {
        try {
            byte[] data = azureBlobService.downloadBlob(blobName, vehicleId);
            return Response.ok(data)
                    .header("Content-Disposition", "attachment; filename=\"" + blobName + "\"")
                    .build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("error", e.getMessage())).build();
        } catch (Exception e) {
            log.error("Download failed", e);
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }

}
