# Service Layer Template

## Overview

This document describes the structure and implementation guidelines for creating a **Service Layer** for feature-based modules (e.g., Equipment Management).  
Each service encapsulates business logic and acts as a bridge between the **API layer (resources)** and the **domain/repository layers**.

---

## Service Structure

A typical service should include:

- A private final repository dependency (e.g., `FeatureRepository`).
- Methods for CRUD operations and optional soft-delete logic.
- Domain mapping using corresponding `Mapper` classes.

---

## Example Template

### Service Class

```java
@ApplicationScoped
public class <Feature>Service {

    private final <Feature>Repository <feature>Repository;

    @Inject
    public <Feature>Service(<Feature>Repository <feature>Repository) {
        this.<feature>Repository = <feature>Repository;
    }

    public <Feature> create<Feature>(Create<Feature>Request request) {
        <Feature> feature = <Feature>Mapper.toDomain(request);
        return <feature>Repository.save(feature);
    }

    public <Feature> find<Feature>ById(Long id) {
        return <feature>Repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("<Feature> not found"));
    }

    public <Feature> update<Feature>(Long id, Create<Feature>Request request) {
        <Feature> feature = <Feature>Mapper.toDomain(request);
        feature.setId(id);
        return <feature>Repository.update(feature);
    }

    public void delete<Feature>(Long id) {
        <feature>Repository.deleteById(id);
    }

    public void softDelete<Feature>(Long id) {
        <feature>Repository.softDeleteById(id);
    }
}
```

---

## Notes

- The `<Feature>Mapper` should handle mapping between DTOs and domain models.
- The repository should encapsulate persistence logic (e.g., Panache, JPA).
- Exceptions such as `ResourceNotFoundException` should be handled consistently across all services.
- Service methods should remain transactional if they modify the database.

---

## Example Usage

In a resource class:

```java
@Path("/equipment")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EquipmentResource {

    @Inject
    EquipmentService equipmentService;

    @POST
    public Response createEquipment(CreateEquipmentRequest request) {
        Equipment equipment = equipmentService.createEquipment(request);
        return Response.status(Response.Status.CREATED).entity(equipment).build();
    }
}
```

---

**Author:** Auto-generated template for service layer architecture.
**Version:** 1.0
