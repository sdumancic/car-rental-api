package car.rental.core.users.service;

import car.rental.core.users.dto.CreateUserRequest;
import car.rental.core.users.dto.LoginUserRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Slf4j
public class KeycloakUserService {

    @Inject
    Keycloak keycloak;

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String serverUrl;

    @ConfigProperty(name = "quarkus.oidc.client-id")
    String clientId;

    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    String clientSecret;

    public String login(LoginUserRequest request) {
        String keycloakUrl = serverUrl + "/protocol/openid-connect/token";
        Client client = ClientBuilder.newClient();
        Form form = new Form();
        form.param("grant_type", "password");
        form.param("client_id", clientId);
        form.param("client_secret", clientSecret);
        form.param("username", request.getUsername());
        form.param("password", request.getPassword());
        Response response = client.target(keycloakUrl)
                .request(MediaType.APPLICATION_FORM_URLENCODED)
                .post(Entity.form(form));
        String json = response.readEntity(String.class);
        response.close();
        client.close();
        try {
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {
            });
            if (map.containsKey("access_token")) {
                return map.get("access_token").toString();
            } else {
                throw new RuntimeException("Login failed: " + json);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String createUserInKeycloak(CreateUserRequest request) {
        try {
            RealmResource realmResource = keycloak.realm("myrealm");
            UsersResource usersResource = realmResource.users();

            UserRepresentation user = new UserRepresentation();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEnabled(true);

            // Set password
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(request.getPassword());
            credential.setTemporary(false);
            user.setCredentials(Collections.singletonList(credential));

            // Create user
            Response response = usersResource.create(user);
            log.info("Keycloak create user response status: {}", response.getStatus());

            // Get the user ID by searching for the created user
            List<UserRepresentation> foundUsers = usersResource.search(request.getUsername());
            if (foundUsers.isEmpty()) {
                throw new RuntimeException("Failed to find created user in Keycloak");
            }
            String userId = foundUsers.getFirst().getId();

            // Assign the "user" role to the created user
            RoleRepresentation userRole = realmResource.roles().get("user").toRepresentation();
            usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(userRole));

            log.info("User created in Keycloak with ID: {} and assigned 'user' role", userId);
            return userId;
        } catch (Exception e) {
            log.error("Failed to create user in Keycloak: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create user in Keycloak", e);
        }
    }
}
