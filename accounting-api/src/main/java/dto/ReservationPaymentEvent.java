package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPaymentEvent {
    private Long reservationId;
    private Long userId;
    private Long vehicleId;
    private Instant startDate;
    private Instant endDate;
    private BigDecimal price;
    private String status;
    private Instant dateCreated;
    private Instant dateModified;

    /*@Override
    public String toString() {
        return "ReservationPaymentEvent{" +
                "reservationId='" + reservationId + '\'' +
                ", userId=" + userId +
                ", vehicleId=" + vehicleId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", price=" + price +
                ", status=" + status +
                ", dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                '}';
    }*/
}

