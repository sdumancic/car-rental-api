package car.rental.core.vehiclemedia.api;

import car.rental.core.azure.dto.FileUploadForm;
import car.rental.core.azure.dto.UploadResult;
import car.rental.core.azure.service.AzureBlobService;
import car.rental.core.vehiclemedia.domain.model.VehicleMedia;
import car.rental.core.vehiclemedia.dto.CreateVehicleMediaRequest;
import car.rental.core.vehiclemedia.service.VehicleMediaService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequestScoped
@Path("/v1/vehicles/{vehicleId}/media")
public class VehicleMediaResource {
    @Inject
    VehicleMediaService vehicleMediaService;

    @Inject
    AzureBlobService azureBlobService;

    @PathParam("vehicleId")
    private Long vehicleId;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createVehicleMedia(@Valid CreateVehicleMediaRequest request) {
        request.setVehicleId(vehicleId);
        VehicleMedia vehicleMedia = vehicleMediaService.createVehicleMedia(request);
        return Response.status(Response.Status.CREATED)
                .entity(vehicleMedia)
                .build();
    }

    @POST
    @Path("{id}/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMediaToVehicle(@MultipartForm FileUploadForm form, @PathParam("id") Long id) {
        VehicleMedia vehicleMedia = vehicleMediaService.findById(id);
        UploadResult result = azureBlobService.uploadMediaForVehicle(form.fileInput, form.fileName, vehicleMedia.getVehicle().getId());
        vehicleMediaService.setMediaBlobUrl(vehicleMedia, result.getUrl());
        return Response.status(Response.Status.CREATED)
                .entity(vehicleMedia)
                .build();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findMediaByVehicleId() {
        List<VehicleMedia> vehicleMediaList = vehicleMediaService.findAllVehicleMediaForVehicle(vehicleId);
        return Response.status(Response.Status.OK)
                .entity(vehicleMediaList)
                .build();
    }

    @GET
    @Path("{id}/download-link")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDownloadLink(@PathParam("id") Long mediaId) {
        VehicleMedia vehicleMedia = vehicleMediaService.findById(mediaId);
        if (vehicleMedia == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Media not found")
                    .build();
        }
        if (vehicleMedia.getUrl() == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Media not uploaded yet")
                    .build();
        }
        // Extract blob name from URL robustly
        String blobUrl = vehicleMedia.getUrl();
        String marker = "/vehicles/";
        int idx = blobUrl.indexOf(marker);
        if (idx == -1) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Invalid blob URL")
                    .build();
        }
        String encodedBlobName = blobUrl.substring(idx + marker.length());
        String blobName = URLDecoder.decode(encodedBlobName, StandardCharsets.UTF_8);
        // Remove leading vehicleId/ if present
        String vehicleIdPrefix = vehicleMedia.getVehicle().getId() + "/";
        if (blobName.startsWith(vehicleIdPrefix)) {
            blobName = blobName.substring(vehicleIdPrefix.length());
        }
        String downloadUrl;
        try {
            downloadUrl = azureBlobService.generateDownloadLink(vehicleMedia.getVehicle().getId(), blobName);
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Blob not found")
                    .build();
        }
        return Response.status(Response.Status.OK)
                .entity(downloadUrl)
                .build();
    }

    @GET
    @Path("{id}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadMedia(@PathParam("id") Long mediaId) {
        try {
            byte[] content = vehicleMediaService.downloadMedia(mediaId);
            VehicleMedia vehicleMedia = vehicleMediaService.findById(mediaId);
            // Extract filename from URL
            String blobUrl = vehicleMedia.getUrl();
            String marker = "/vehicles/";
            int idx = blobUrl.indexOf(marker);
            String encodedBlobName = blobUrl.substring(idx + marker.length());
            String blobName = URLDecoder.decode(encodedBlobName, StandardCharsets.UTF_8);
            // Remove leading vehicleId/ if present
            String vehicleIdPrefix = vehicleMedia.getVehicle().getId() + "/";
            if (blobName.startsWith(vehicleIdPrefix)) {
                blobName = blobName.substring(vehicleIdPrefix.length());
            }
            String filename = blobName;
            return Response.ok(content)
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Blob not found")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to download media")
                    .build();
        }
    }
}
