package car.rental.core.pricing.api;

import car.rental.core.pricing.domain.model.PricingCategory;
import car.rental.core.pricing.dto.CreatePricingCategoryRequest;
import car.rental.core.pricing.service.PricingCategoryService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@RequestScoped
@Path("/v1/pricing-categories")
public class PricingCategoryResource {

    @Inject
    PricingCategoryService pricingCategoryService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPricingCategory(@Valid CreatePricingCategoryRequest request) {
        try {
            PricingCategory pricingCategory = pricingCategoryService.createPricingCategory(request);
            return Response.status(Response.Status.CREATED)
                    .entity(pricingCategory)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllPricingCategories() {
        List<PricingCategory> pricingCategories = pricingCategoryService.findAllPricingCategories();
        return Response.status(Response.Status.OK)
                .entity(pricingCategories)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPricingCategoryById(@PathParam("id") Long id) {
        PricingCategory pricingCategory = pricingCategoryService.findPricingCategoryById(id);
        if (pricingCategory == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK)
                .entity(pricingCategory)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePricingCategory(@PathParam("id") Long id, @Valid CreatePricingCategoryRequest request) {
        try {
            PricingCategory pricingCategory = pricingCategoryService.updatePricingCategory(id, request);
            return Response.ok(pricingCategory).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deletePricingCategory(@PathParam("id") Long id) {
        pricingCategoryService.deletePricingCategoryById(id);
        return Response.noContent().build();
    }
}
