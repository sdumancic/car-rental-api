package car.rental.core.azure.api;

import car.rental.core.azure.service.AzureBlobService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Path("/videos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class VideoUploadResource {

    @Inject
    AzureBlobService azureBlobService;

    @POST
    @Path("/init-upload")
    public Response initUpload(InitUploadRequest req) {
        String blobName = azureBlobService.generateBlobName(req.fileName);
        List<String> urls = azureBlobService.generateBlockUploadUrls("videos", blobName, req.totalParts);
        return Response.ok(new InitUploadResponse(blobName, urls)).build();
    }

    @POST
    @Path("/complete-upload")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response completeUpload(CompleteUploadRequest req) {
        String response = azureBlobService.commitBlockList("videos", req.blobName, req.totalParts);
        return Response.ok(Map.of("response", response)).build();
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile(@MultipartForm FileUploadForm form) {
        try {
            AzureBlobService.UploadResult result = azureBlobService.uploadMethod(form.fileInput, form.fileName);
            return Response.ok(Map.of(
                    "container", result.container,
                    "blobName", result.blobName,
                    "url", result.url,
                    "mediaCategory", result.mediaCategory,
                    "originalFileName", result.originalFileName
            )).build();
        } catch (Exception e) {
            log.error("Upload failed", e);
            return Response.serverError().entity(Map.of("error", e.getMessage())).build();
        }
    }

    public static class InitUploadRequest {
        public String fileName;
        public int totalParts;
    }

    public static class InitUploadResponse {
        public String blobName;
        public List<String> urls;

        public InitUploadResponse(String blobName, List<String> urls) {
            this.blobName = blobName;
            this.urls = urls;
        }
    }

    public static class CompleteUploadRequest {
        public String blobName;
        public int totalParts;
    }

    public static class FileUploadForm {
        @FormParam("file")
        public InputStream fileInput;
        @FormParam("fileName")
        public String fileName;
    }
}
