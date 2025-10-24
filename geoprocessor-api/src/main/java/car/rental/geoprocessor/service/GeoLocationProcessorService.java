package car.rental.geoprocessor.service;

import car.rental.geoprocessor.dto.VehicleLocationEvent;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
@Startup
public class GeoLocationProcessorService {

    @Inject
    RestClient restClient;

    private volatile boolean indexInitialized = false;
    private final Object indexLock = new Object();

    public void init(@Observes StartupEvent event) {
        ensureIndexWithMapping();
    }

    private void ensureIndexWithMapping() {
        if (indexInitialized) {
            return;
        }

        synchronized (indexLock) {
            if (indexInitialized) {
                return;
            }

            try {
                createOrUpdateIndexMapping();
                indexInitialized = true;
                log.info("Elasticsearch index 'vehicle-locations' initialized with geo_point mapping");
            } catch (IOException e) {
                log.error("Failed to create Elasticsearch index mapping", e);
                throw new RuntimeException("Could not initialize Elasticsearch index", e);
            }
        }
    }

    private void createOrUpdateIndexMapping() throws IOException {
        // Check if index exists
        boolean indexExists = false;
        try {
            Request checkRequest = new Request("HEAD", "/vehicle-locations");
            final Response response = restClient.performRequest(checkRequest);
            log.info("Elasticsearch index check response status: {}", response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                indexExists = true;
                log.info("Index 'vehicle-locations' already exists, skipping creation");
            } else {
                log.info("Index 'vehicle-locations' does not exist, creating...");
            }
        } catch (Exception e) {
            log.info("Index 'vehicle-locations' does not exist, creating...");
        }

        if (indexExists) {
            // Index already exists, nothing to do
            return;
        }

        // Create index with geo_point mapping
        Request request = new Request("PUT", "/vehicle-locations");

        JsonObject mapping = new JsonObject()
                .put("mappings", new JsonObject()
                        .put("properties", new JsonObject()
                                .put("id", new JsonObject().put("type", "keyword"))
                                .put("reservationId", new JsonObject().put("type", "keyword"))
                                .put("vehicleId", new JsonObject().put("type", "keyword"))
                                .put("userId", new JsonObject().put("type", "keyword"))
                                .put("timestamp", new JsonObject().put("type", "date"))
                                .put("location", new JsonObject().put("type", "geo_point"))
                        )
                );

        request.setJsonEntity(mapping.encode());
        restClient.performRequest(request);
        log.info("Created index 'vehicle-locations' with geo_point mapping for location field");
    }

    @Incoming("vehicle-location-events")
    public CompletionStage<Void> consume(Message<VehicleLocationEvent> msg) {
        // access record metadata
        var metadata = msg.getMetadata(IncomingKafkaRecordMetadata.class).orElseThrow();
        log.info("Received message with key: {}, partition: {}, offset: {} topic {}",
                metadata.getKey(),
                metadata.getPartition(),
                metadata.getOffset(),
                metadata.getTopic());
        // process the message payload.
        VehicleLocationEvent eventData = msg.getPayload();
        log.info("Processing Location Event for vehicle ID {}, reservation ID: {}", eventData.getVehicleId(), eventData.getReservationId());
        // Acknowledge the incoming message (commit the offset)
        try {
            index(eventData);
        } catch (IOException e) {
            log.error("Error while processing Location Event for vehicle ID {}", eventData.getVehicleId(), e);
        }
        return msg.ack();
    }

    public void index(VehicleLocationEvent event) throws IOException {
        ensureIndexWithMapping(); // Ensure mapping exists before indexing
        Request request = new Request(
                "PUT",
                "/vehicle-locations/_doc/" + event.getId());
        request.setJsonEntity(JsonObject.mapFrom(event.toElasticDocument()).toString());
        restClient.performRequest(request);
    }

    public void index(List<VehicleLocationEvent> list) throws IOException {

        var entityList = new ArrayList<JsonObject>();

        for (var event : list) {

            entityList.add(new JsonObject().put("index", new JsonObject()
                    .put("_index", "vehicle-locations").put("_id", event.getId())));
            entityList.add(JsonObject.mapFrom(event.toElasticDocument()));
        }

        Request request = new Request(
                "POST", "vehicle-locations/_bulk?pretty");
        request.setEntity(new StringEntity(
                toNdJsonString(entityList),
                ContentType.create("application/x-ndjson")));
        restClient.performRequest(request);
    }

    public void delete(List<String> identityList) throws IOException {

        var entityList = new ArrayList<JsonObject>();

        for (var id : identityList) {
            entityList.add(new JsonObject().put("delete",
                    new JsonObject().put("_index", "vehicle-locations").put("_id", id)));
        }

        Request request = new Request(
                "POST", "vehicle-locations/_bulk?pretty");
        request.setEntity(new StringEntity(
                toNdJsonString(entityList),
                ContentType.create("application/x-ndjson")));
        restClient.performRequest(request);
    }

    public VehicleLocationEvent get(String id) throws IOException {
        Request request = new Request(
                "GET",
                "/vehicle-locations/_doc/" + id);
        Response response = restClient.performRequest(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        JsonObject json = new JsonObject(responseBody);
        return json.getJsonObject("_source").mapTo(VehicleLocationEvent.class);
    }

    public List<VehicleLocationEvent> searchByVehicleId(String vehicleId) throws IOException {
        return search("vehicleId", vehicleId);
    }

    public List<VehicleLocationEvent> searchByReservationId(String reservationId) throws IOException {
        return search("reservationId", reservationId);
    }

    private List<VehicleLocationEvent> search(String term, String match) throws IOException {
        Request request = new Request(
                "GET",
                "/vehicle-locations/_search");
        //construct a JSON query like {"query": {"match": {"<term>": "<match"}}
        JsonObject termJson = new JsonObject().put(term, match);
        JsonObject matchJson = new JsonObject().put("match", termJson);
        JsonObject queryJson = new JsonObject().put("query", matchJson);
        request.setJsonEntity(queryJson.encode());
        Response response = restClient.performRequest(request);
        String responseBody = EntityUtils.toString(response.getEntity());

        JsonObject json = new JsonObject(responseBody);
        JsonArray hits = json.getJsonObject("hits").getJsonArray("hits");
        List<VehicleLocationEvent> results = new ArrayList<>(hits.size());
        for (int i = 0; i < hits.size(); i++) {
            JsonObject hit = hits.getJsonObject(i);
            VehicleLocationEvent fruit = hit.getJsonObject("_source").mapTo(VehicleLocationEvent.class);
            results.add(fruit);
        }
        return results;
    }

    private static String toNdJsonString(List<JsonObject> objects) {
        return objects.stream()
                .map(JsonObject::encode)
                .collect(Collectors.joining("\n", "", "\n"));
    }
}
