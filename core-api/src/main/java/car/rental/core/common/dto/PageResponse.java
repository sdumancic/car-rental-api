package car.rental.core.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PageResponse<T> {
    private List<T> data;
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
