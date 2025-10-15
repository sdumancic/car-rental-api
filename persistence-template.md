# 🧱 Persistence Layer Instructions

## 🎯 Purpose
The **Persistence Layer** provides the bridge between the **Domain** and the **Database**.  
It defines and implements repositories that handle CRUD operations, entity management, and data mapping via JPA/Hibernate.

In this architecture:
- **Entities** represent the database tables.
- **Entity repositories** extend `PanacheRepository<T>` for database operations.
- **Panache repositories** implement domain-level repository interfaces and handle domain ↔ entity mapping.

---

## 📁 Location
```
src/main/java/<base_package>/<feature>/infrastructure/persistence/
```

Typical structure:
```
infrastructure/persistence/
 ├── <Feature>Entity.java
 ├── <Feature>EntityRepository.java
 └── Panache<Feature>Repository.java
```

---

## 🧩 Components

### 1. Entity
**File:** `<Feature>Entity.java`  
Represents the database table structure. Extends a base entity with shared fields like `id`, `dateCreated`, `dateModified`.

```java
package <base_package>.<feature>.infrastructure.persistence;

import <base_package>.core.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "<feature>")
@ToString
public class <Feature>Entity extends BaseEntity {

    private String name;
    private String description;
    private Boolean active;
}
```

**Guidelines:**
- Use JPA annotations (`@Entity`, `@Table`, etc.).
- Use Lombok (`@Getter`, `@Setter`, `@ToString`) for boilerplate reduction.
- Inherit from a `BaseEntity` containing audit fields (`id`, `dateCreated`, `dateModified`, etc.).

---

### 2. Entity Repository
**File:** `<Feature>EntityRepository.java`  
Provides direct access to the database through **Panache**.

```java
package <base_package>.<feature>.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class <Feature>EntityRepository implements PanacheRepository<<Feature>Entity> {

}
```

**Guidelines:**
- Annotate with `@ApplicationScoped` for CDI lifecycle.
- Extend `PanacheRepository<Entity>` to gain CRUD and query helpers (`find`, `listAll`, `persist`, etc.).
- Keep this class **thin** — do not implement business logic here.

---

### 3. Panache Repository Implementation
**File:** `Panache<Feature>Repository.java`  
Implements the **domain repository interface** and handles **mapping** between domain objects and persistence entities.

```java
package <base_package>.<feature>.infrastructure.persistence;

import <base_package>.<feature>.domain.model.<Feature>;
import <base_package>.<feature>.domain.repository.<Feature>Repository;
import <base_package>.<feature>.infrastructure.mapper.<Feature>Mapper;
import <base_package>.core.common.exception.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class Panache<Feature>Repository implements <Feature>Repository {

    private final <Feature>EntityRepository <feature>EntityRepository;

    @Override
    public Optional<<Feature>> findById(Long id) {
        return <feature>EntityRepository.findByIdOptional(id).map(<Feature>Mapper::toDomain);
    }

    @Override
    public List<<Feature>> findAll() {
        return <feature>EntityRepository.listAll().stream()
                .map(<Feature>Mapper::toDomain)
                .toList();
    }

    @Transactional
    @Override
    public <Feature> save(<Feature> feature) {
        <Feature>Entity entity = <Feature>Mapper.toEntity(feature);
        entity.setDateCreated(Instant.now());
        entity.setDateModified(Instant.now());
        <feature>EntityRepository.persist(entity);
        return <Feature>Mapper.toDomain(entity);
    }

    @Transactional
    @Override
    public <Feature> update(<Feature> feature) {
        <Feature>Entity entity = <feature>EntityRepository.findById(feature.getId());
        if (entity == null) {
            throw new ResourceNotFoundException("<Feature> not found for id: " + feature.getId());
        }
        <Feature>Mapper.updateEntity(entity, feature);
        return <Feature>Mapper.toDomain(entity);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        <Feature>Entity entity = <feature>EntityRepository.findById(id);
        if (entity != null) {
            <feature>EntityRepository.delete(entity);
        }
    }

    @Transactional
    @Override
    public void softDeleteById(Long id) {
        <Feature>Entity entity = <feature>EntityRepository.findById(id);
        if (entity != null) {
            entity.setActive(false);
            entity.setDateModified(Instant.now());
            <feature>EntityRepository.persist(entity);
        }
    }
}
```

---

## 🧠 Design Guidelines

- **Domain-first**: Always interact with domain objects (`<Feature>`) in higher layers.
- **Mapping**: Use mappers (`<Feature>Mapper`) for conversions between domain and entity models.
- **Transactional safety**: Annotate write methods (`save`, `update`, `deleteById`, `softDeleteById`) with `@Transactional`.
- **Error handling**: Throw domain-level exceptions like `ResourceNotFoundException` for missing records.
- **Soft deletes**: Use a boolean flag (e.g., `active`) to logically delete data.
- **Logging**: Use `@Slf4j` for repository-level logging.
