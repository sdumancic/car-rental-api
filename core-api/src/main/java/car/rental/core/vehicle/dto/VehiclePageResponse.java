package car.rental.core.vehicle.dto;

import car.rental.core.vehicle.domain.model.Vehicle;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class VehiclePageResponse {
    private List<Vehicle> data;
    private PageMetadata metadata;

    @Getter
    @Setter
    @Builder
    public static class PageMetadata {
        private int page;
        private int size;
        private long totalRecords;
        private String currentPageUrl;
        private String prevPageUrl;
        private String nextPageUrl;
    }
}
