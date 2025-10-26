package car.rental.core.vehicledamage.dto;

import jakarta.ws.rs.FormParam;

public class ImageUploadForm {
    @FormParam("file")
    public byte[] file;
}
