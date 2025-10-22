import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ReservationPaymentEvent;
import io.quarkus.runtime.Startup;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.concurrent.CompletionStage;

@ApplicationScoped
@Slf4j
@Startup
public class PaymentProcessor {

    @Inject
    ObjectMapper objectMapper;


    @Incoming("payments")
    @Blocking
    public void process(ReservationPaymentEvent paymentEvent) throws Exception {
        Thread.sleep(200);

        //ReservationPaymentEvent paymentEvent = objectMapper.readValue(message, ReservationPaymentEvent.class);

        log.info("Processing Payment Request for Reservation ID: {}", paymentEvent.getReservationId());
        log.info("User ID: {}, Vehicle ID: {}, Price: {}, Status: {}",
                paymentEvent.getUserId(),
                paymentEvent.getVehicleId(),
                paymentEvent.getPrice(),
                paymentEvent.getStatus());
    }

    @Incoming("payments")
    public CompletionStage<Void> consume(Message<ReservationPaymentEvent> msg) {
        // access record metadata
        var metadata = msg.getMetadata(IncomingKafkaRecordMetadata.class).orElseThrow();
        log.info("Received message with key: {}, partition: {}, offset: {} topic {}",
                metadata.getKey(),
                metadata.getPartition(),
                metadata.getOffset(),
                metadata.getTopic());
        // process the message payload.
        ReservationPaymentEvent payment = msg.getPayload();
        log.info("Processing Payment Request for Reservation ID: {}", payment.getReservationId());
        // Acknowledge the incoming message (commit the offset)
        return msg.ack();
    }
}
