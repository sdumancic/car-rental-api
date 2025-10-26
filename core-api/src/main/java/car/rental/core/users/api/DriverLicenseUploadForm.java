package car.rental.core.users.api;

import jakarta.ws.rs.FormParam;

import java.io.InputStream;

public class DriverLicenseUploadForm {

    @FormParam("file")
    private InputStream file;

    @FormParam("fileName")
    private String fileName;

    public InputStream getFile() {
        return file;
    }

    public void setFile(InputStream file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
