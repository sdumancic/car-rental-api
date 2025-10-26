package car.rental.core.vehicledamage.dto;

import java.util.List;

public class DetectionResponse {
    public static class Detection {
        public String label;
        public double confidence;
    }

    public List<Detection> detections;
}
