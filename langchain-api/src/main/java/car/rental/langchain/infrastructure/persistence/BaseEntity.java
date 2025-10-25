package car.rental.langchain.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_created", columnDefinition = "datetime2")
    private Instant dateCreated;
    @Column(name = "date_modified", columnDefinition = "datetime2")
    private Instant dateModified;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.dateCreated = now;
        this.dateModified = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModified = Instant.now();
    }
}
