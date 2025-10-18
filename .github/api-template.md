# API Layer Template

## Purpose

The API layer exposes RESTful endpoints for managing `<Feature>` entities.  
It acts as a thin layer between the HTTP interface and the Service layer, responsible for:
- Request validation
- Mapping query/path parameters to DTOs
- Building HTTP responses

---

## File

`<Feature>Resource.java`

## Package

`car.rental.core.<feature>.api`

---

## Class Declaration

```java
@RequestScoped
@Path("/v1/<feature-plural>")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class <Feature>Resource {
```
- Annotated with `@RequestScoped` for per-request lifecycle.
- All endpoints consume and produce JSON.
- The base path follows REST versioning conventions (`/v1/`).

---

## Dependencies

```java
@Inject
<Feature>Service <feature>Service;
```
Injects the corresponding Service layer to handle business logic.

---

## Endpoints

### Create `<Feature>`

```java
@POST
public Response create<Feature>(@Valid Create<Feature>Request request) {
    <Feature> <feature> = <feature>Service.create<Feature>(request);
    return Response.status(Response.Status.CREATED)
            .entity(<feature>)
            .build();
}
```
- Validates incoming request (`@Valid`).
- Delegates creation to the service layer.
- Returns 201 Created with the created entity.

---

### Find `<Feature>` by ID

```java
@GET
@Path("/{id}")
public Response find<Feature>ById(@PathParam("id") Long id) {
    <Feature> <feature> = <feature>Service.find<Feature>ById(id);
    return Response.ok(<feature>).build();
}
```
- Retrieves an entity by its ID.
- Returns 200 OK and entity data.
- If not found, throws `ResourceNotFoundException` from service.

---

### Find All / Search

```java
@GET
public Response find<Feature>s(
        @QueryParam("filterField1") String filterField1,
        @QueryParam("filterField2") String filterField2,
        @QueryParam("sort") String sort,
        @QueryParam("page") @DefaultValue("0") Integer page,
        @QueryParam("size") @DefaultValue("10") Integer size) {

    Query<Feature>Request query = new Query<Feature>Request();
    query.setFilterField1(filterField1);
    query.setFilterField2(filterField2);
    query.setSort(sort);
    query.setPage(page);
    query.setSize(size);

    PageResponse<<Feature>> response = <feature>Service.find<Feature>s(query);
    return Response.ok(response).build();
}
```
- Supports pagination and sorting.
- Builds `Query<Feature>Request` DTO from query params.
- Returns paginated response object.

---

### Update `<Feature>`

```java
@PUT
@Path("/{id}")
public Response update<Feature>(
        @PathParam("id") Long id,
        @Valid Create<Feature>Request request) {

    <Feature> updated = <feature>Service.update<Feature>(id, request);
    return Response.ok(updated).build();
}
```
- Updates an existing entity.
- Returns updated entity with 200 OK.

---

### Delete `<Feature>` (Soft Delete)

```java
@DELETE
@Path("/{id}")
public Response delete<Feature>(@PathParam("id") Long id) {
    <feature>Service.softDelete<Feature>(id);
    return Response.noContent().build();
}
```
- Performs a soft delete (marks as inactive).
- Returns 204 No Content.

---

## Summary of Responsibilities

| Responsibility | Layer |
|----------------|--------|
| Request validation | API |
| Parameter parsing | API |
| DTO creation | API |
| Business logic | Service |
| Persistence | Repository |

---

## Example

For `Vehicle`, the path is `/v1/vehicles`, and all endpoints mirror this template:
- `POST /v1/vehicles`
- `GET /v1/vehicles/{id}`
- `GET /v1/vehicles`
- `PUT /v1/vehicles/{id}`
- `DELETE /v1/vehicles/{id}`
