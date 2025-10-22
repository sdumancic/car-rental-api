package car.rental.core.reservation.dto;

import car.rental.core.reservation.domain.model.ReservationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class QueryReservationRequest {
    private Integer page;
    private Integer size;
    private Long userId;
    private Long vehicleId;
    private Instant startDate;
    private Instant endDate;
    private ReservationStatus status;
    private String sort;
    private Integer notCompleted;

    public QueryReservationRequest() {
        // Default constructor for builder
    }

    public QueryReservationRequest(Integer page, Integer size, Long userId, Long vehicleId, Instant startDate, Instant endDate, ReservationStatus status, String sort, Integer notCompleted) {
        this.page = page;
        this.size = size;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.sort = sort;
        this.notCompleted = notCompleted;
    }

    public static class Builder {
        private Integer page;
        private Integer size;
        private Long userId;
        private Long vehicleId;
        private Instant startDate;
        private Instant endDate;
        private ReservationStatus status;
        private String sort;
        private Integer notCompleted;

        public Builder page(Integer page) {
            this.page = page;
            return this;
        }

        public Builder size(Integer size) {
            this.size = size;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder vehicleId(Long vehicleId) {
            this.vehicleId = vehicleId;
            return this;
        }

        public Builder startDate(Instant startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(Instant endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder status(ReservationStatus status) {
            this.status = status;
            return this;
        }

        public Builder sort(String sort) {
            this.sort = sort;
            return this;
        }

        public Builder notCompleted(Integer notCompleted) {
            this.notCompleted = notCompleted;
            return this;
        }

        public QueryReservationRequest build() {
            QueryReservationRequest request = new QueryReservationRequest();
            request.setPage(this.page);
            request.setSize(this.size);
            request.setUserId(this.userId);
            request.setVehicleId(this.vehicleId);
            request.setStartDate(this.startDate);
            request.setEndDate(this.endDate);
            request.setStatus(this.status);
            request.setSort(this.sort);
            request.setNotCompleted(this.notCompleted);
            return request;
        }
    }

}
