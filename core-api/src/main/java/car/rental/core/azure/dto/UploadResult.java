package car.rental.core.azure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UploadResult {
    private String container;
    private String blobName;
    private String url;
    private String mediaCategory; // image | video | other
    private String originalFileName;
}
