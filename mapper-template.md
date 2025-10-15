# 🧭 Mapper Layer Instructions

## Purpose
The **Mapper** is responsible for converting between different layers of the application:
- **API layer DTOs** → **Domain models**
- **Domain models** → **Persistence entities**
- **Persistence entities** → **Domain models**

This ensures that changes in one layer (like persistence) do not leak into another (like the domain or API).

---

## 📁 Location
`src/main/java/<base_package>/<feature>/infrastructure/mapper/<Feature>Mapper.java`

---

## 🧱 Class Structure

```java
package <base_package>.<feature>.infrastructure.mapper;

import <base_package>.<feature>.domain.model.<Feature>;
import <base_package>.<feature>.dto.Create<Feature>Request;
import <base_package>.<feature>.infrastructure.persistence.<Feature>Entity;

public class <Feature>Mapper {

    // --- API → Domain ---
    public static <Feature> toDomain(Create<Feature>Request request) {
        if (request == null) {
            return null;
        }
        return <Feature>.builder()
                .id(null)
                .name(request.getName())
                .description(request.getDescription())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
    }

    // --- Domain → Entity ---
    public static <Feature>Entity toEntity(<Feature> domain) {
        if (domain == null) {
            return null;
        }
        <Feature>Entity entity = new <Feature>Entity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setActive(domain.getActive());
        return entity;
    }

    // --- Entity → Domain ---
    public static <Feature> toDomain(<Feature>Entity entity) {
        if (entity == null) {
            return null;
        }
        return <Feature>.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .active(entity.getActive())
                .build();
    }

    // --- Update Existing Entity from Domain ---
    public static void updateEntity(<Feature>Entity entity, <Feature> domain) {
        if (entity == null || domain == null) {
            return;
        }
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setActive(domain.getActive());
    }
}
```

---

## 🧩 Guidelines

- **Static methods:** All methods are static — no instantiation needed.
- **Null-safety:** Always check for `null` to prevent `NullPointerException`.
- **Immutable domain models:** The domain objects should be immutable, typically using a builder pattern.
- **Entity updates:** Use `updateEntity()` to apply changes from a domain object to an existing entity (for update operations).
- **Optional fields:** Handle optional values safely, applying defaults when needed.
- **Separation:** Keep domain and entity mapping logic distinct from service logic.
