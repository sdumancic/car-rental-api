package car.rental.core.users.api;

import car.rental.core.azure.dto.UploadResult;
import car.rental.core.users.domain.model.User;
import car.rental.core.users.dto.CreateUserRequest;
import car.rental.core.users.dto.QueryUserRequest;
import car.rental.core.users.service.UserService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

@RequestScoped
@Path("/v1/users")
public class UserResource {
    @Inject
    UserService userService;

    @Inject
    UriInfo uriInfo;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(CreateUserRequest request) {
        User user = userService.createUser(request);
        return Response.status(Response.Status.CREATED)
                .entity(user)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") Long id, @Valid CreateUserRequest request) {
        User user = userService.updateUser(id, request);
        return Response.ok(user).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        userService.softDeleteUser(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/driver-license")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadDriverLicense(@PathParam("id") Long id, @MultipartForm DriverLicenseUploadForm form) {
        try {
            UploadResult result = userService.uploadDriverLicense(id, form.getFile(), form.getFileName());
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to upload driver license: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}/driver-license")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadDriverLicense(@PathParam("id") Long id) {
        try {
            byte[] data = userService.downloadDriverLicense(id);
            return Response.ok(data)
                    .header("Content-Disposition", "attachment; filename=\"driver-license\"")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Driver license not found: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}/driver-license/link")
    @Produces(MediaType.TEXT_PLAIN)
    public Response generateDriverLicenseDownloadLink(@PathParam("id") Long id) {
        try {
            String link = userService.generateDriverLicenseDownloadLink(id);
            return Response.ok(link).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Driver license not found: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUsers(
            @QueryParam("name") String name,
            @QueryParam("email") String email,
            @QueryParam("role") String role,
            @QueryParam("sort") String sort,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("size") @DefaultValue("10") Integer size) {
        QueryUserRequest query = new QueryUserRequest();
        query.setName(name);
        query.setEmail(email);
        query.setRole(role);
        query.setSort(sort);
        query.setPage(page);
        query.setSize(size);
        var response = userService.findUsers(query, uriInfo);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

}
