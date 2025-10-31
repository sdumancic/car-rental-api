package car.rental.langchain.infrastructure.restclient;

import car.rental.langchain.domain.model.DamageDetectionResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Slf4j
public class DamageDetectionClientWrapper {

    @Inject
    @RestClient
    DamageDetectionRestClient damageDetectionRestClient;

    @Timeout(30000) // 30 seconds timeout
    @Retry(
            maxRetries = 3,
            delay = 1000,
            jitter = 500,
            retryOn = {Exception.class}
    )
    @Fallback(fallbackMethod = "detectDamageFallback")
    public DamageDetectionResponse detectDamage(File imageFile) {
        log.info("Calling damage detection API for image: {}", imageFile.getName());
        DamageDetectionResponse response = damageDetectionRestClient.detectDamage(imageFile);
        log.info("Damage detection completed. Level: {}, Confidence: {}",
                response.getDamageLevel(), response.getMaxConfidence());
        return response;
    }

    public DamageDetectionResponse detectDamageFallback(File imageFile) {
        log.error("Fallback triggered for damage detection. Damage detection service is unavailable for image: {}",
                imageFile.getName());
        log.error("Please ensure the damage detection service is running on http://localhost:8000");

        // Return a fallback response indicating service unavailable
        DamageDetectionResponse fallbackResponse = new DamageDetectionResponse();
        List<DamageDetectionResponse.Detection> detections = new ArrayList<>();

        // Create a detection indicating unknown/service unavailable
        DamageDetectionResponse.Detection detection = new DamageDetectionResponse.Detection();
        detection.setLabel("service-unavailable");
        detection.setConfidence(0.0);
        detections.add(detection);

        fallbackResponse.setDetections(detections);

        log.warn("Returning fallback response: NONE damage level due to service unavailability");
        return fallbackResponse;
    }
}

