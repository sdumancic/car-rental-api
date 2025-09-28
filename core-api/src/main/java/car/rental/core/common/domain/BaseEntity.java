package car.rental.core.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
