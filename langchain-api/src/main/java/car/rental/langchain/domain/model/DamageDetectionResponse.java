package car.rental.langchain.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DamageDetectionResponse {
    private List<Detection> detections;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detection {
        private String label;
        private double confidence;
    }

    public DamageLevel getDamageLevel() {
        if (detections == null || detections.isEmpty()) {
            return DamageLevel.NONE;
        }

        // Find the detection with highest confidence
        Detection maxConfidence = detections.stream()
                .max((d1, d2) -> Double.compare(d1.getConfidence(), d2.getConfidence()))
                .orElseGet(() -> {
                    Detection defaultDetection = new Detection();
                    defaultDetection.setLabel("none");
                    defaultDetection.setConfidence(0.0);
                    return defaultDetection;
                });

        // Determine damage level based on the label
        if (maxConfidence.getLabel().contains("01-minor")) {
            return DamageLevel.MINOR;
        } else if (maxConfidence.getLabel().contains("02-moderate")) {
            return DamageLevel.MODERATE;
        } else if (maxConfidence.getLabel().contains("03-severe")) {
            return DamageLevel.SEVERE;
        } else {
            return DamageLevel.NONE;
        }
    }

    public double getMaxConfidence() {
        if (detections == null || detections.isEmpty()) {
            return 0.0;
        }
        return detections.stream()
                .mapToDouble(Detection::getConfidence)
                .max()
                .orElse(0.0);
    }
}

