# Domain Layer Template

## Purpose

The **domain layer** represents the **core business logic** and **enterprise concepts**.  
It is completely **independent of frameworks** such as Quarkus, Hibernate, or Jakarta EE.  
This layer defines *what* the application does — not *how* it does it.

---

## Key Principles

✅ **Framework-agnostic** — No JPA, CDI, REST, or Quarkus annotations.  
✅ **Business-focused** — Represents business entities and rules.  
✅ **Immutable or controlled mutability** — Domain models should be stable and controlled.  
✅ **Testable** — Can be tested without involving databases or containers.

---

## Structure

```
car.rental.core.<feature>.domain
├── model
│   └── <Feature>.java
├── repository
│   └── <Feature>Repository.java
└── service (optional for domain-level rules)
```

---

## Domain Model Example

`car.rental.core.reservation.domain.model.Reservation`

```java
@Getter
@Setter
@Builder
@ToString
public class Reservation {
    private Long id;
    private User user;
    private Vehicle vehicle;
    private Instant startDate;
    private Instant endDate;
    private BigDecimal price;
    private ReservationStatus status;
    private Instant dateCreated;
    private Instant dateModified;
}
```

### Characteristics

- **No annotations** from JPA or CDI.
- Uses Lombok for simplicity (`@Getter`, `@Setter`, `@Builder`).
- Encapsulates **business meaning** — not persistence.
- May include **business methods** (e.g., `cancel()`, `extend()`, etc.) if domain rules are complex.

---

## Domain Repository Interface

`car.rental.core.reservation.domain.repository.ReservationRepository`

```java
public interface ReservationRepository extends BaseRepository<Reservation> {

    List<Reservation> findByQuery(QueryReservationRequest query);

    long countByQuery(QueryReservationRequest query);

    Reservation update(Reservation reservation);

    void softDeleteById(Long id);

    boolean isVehicleAvailable(Long vehicleId, Instant startDate, Instant endDate, Long excludeReservationId);
}
```

### Characteristics

- Defines **contracts** for persistence without knowing the implementation.
- Only depends on **domain models** and **DTOs** — not on Panache, Hibernate, or Entity classes.
- Implementations live in the **infrastructure layer**.

---

## Responsibilities by Subpackage

| Package | Responsibility |
|----------|----------------|
| `domain.model` | Domain entities and value objects |
| `domain.repository` | Abstract repository interfaces |
| `domain.service` | Domain-level logic (optional) |

---

## Example Directory Layout

```
core/
└── reservation/
    ├── api/
    ├── application/
    ├── domain/
    │   ├── model/
    │   │   └── Reservation.java
    │   ├── repository/
    │   │   └── ReservationRepository.java
    │   └── service/
    ├── infrastructure/
    │   ├── mapper/
    │   └── persistence/
    └── dto/
```

---

## Summary

| Layer | Focus | Depends On |
|--------|--------|-------------|
| **Domain** | Business rules, core models, interfaces | None |
| **Infrastructure** | Technical details, repositories, mapping | Domain |
| **Service** | Use cases, application flow | Domain + Infrastructure |
| **API** | REST endpoints | Service |

---

## Best Practices

- Keep this layer **pure** — no framework imports.
- Use **value objects** for strong typing (e.g., `Money`, `DateRange`).
- Prefer **composition** over inheritance.
- Avoid leaking `Entity` or `DTO` classes here.
- Keep **business invariants** in the model (e.g., reservation date validation).  

