package car.rental.core.users.api;

import jakarta.ws.rs.FormParam;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import java.io.InputStream;

public class DriverLicenseUploadForm {

    @FormParam("file")
    @PartType("application/octet-stream")
    private InputStream file;

    @FormParam("fileName")
    @PartType("text/plain")
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
