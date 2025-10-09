package car.rental.core.azure.dto;

import jakarta.ws.rs.FormParam;

import java.io.InputStream;

public class FileUploadForm {
    @FormParam("file")
    public InputStream fileInput;
    @FormParam("fileName")
    public String fileName;
}
