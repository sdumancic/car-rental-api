package car.rental.core.billingdetails.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

@Entity
@Getter
@Setter
@Immutable
@Subselect(value = """
        SELECT u.id AS user_id, COUNT(b.id) AS billing_details_count
        FROM users u
        LEFT JOIN billing_details b ON u.id = b.user_id
        WHERE b.active = true
        GROUP BY u.id
        """)
@Synchronize({"users", "billing_details"})
public class UserBillingDetailsCount {
    @Id
    private Long userId;
    private Long billingDetailsCount;
}
