package car.rental.core.users.service;

import car.rental.core.users.dto.CreateUserRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
@Slf4j
public class KeycloakUserService {

    @Inject
    Keycloak keycloak;

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
            var response = usersResource.create(user);
            log.info("Keycloak create user response status: {}", response.getStatus());

            // Get the user ID by searching for the created user
            List<UserRepresentation> foundUsers = usersResource.search(request.getUsername());
            if (foundUsers.isEmpty()) {
                throw new RuntimeException("Failed to find created user in Keycloak");
            }
            String userId = foundUsers.get(0).getId();

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
