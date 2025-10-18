package car.rental.core.users.service;

import car.rental.core.users.dto.LoginUserRequest;
import car.rental.core.users.dto.TokenResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
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

import java.util.Map;

@ApplicationScoped
@Slf4j
public class KeycloakTokenService {

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String serverUrl;
    @ConfigProperty(name = "quarkus.oidc.client-id")
    String clientId;
    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    String clientSecret;

    private String tokenEndpoint;

    @PostConstruct
    void init() {
        tokenEndpoint = serverUrl + "/protocol/openid-connect/token";
    }

    public TokenResponse login(LoginUserRequest request) {
        Form form = new Form()
                .param("grant_type", "password")
                .param("client_id", clientId)
                .param("client_secret", clientSecret)
                .param("username", request.getUsername())
                .param("password", request.getPassword());
        String json = post(form);
        return parse(json, "Login failed: ");
    }

    public TokenResponse refresh(String refreshToken) {
        Form form = new Form()
                .param("grant_type", "refresh_token")
                .param("client_id", clientId)
                .param("client_secret", clientSecret)
                .param("refresh_token", refreshToken);
        String json = post(form);
        return parse(json, "Refresh failed: ");
    }

    private String post(Form form) {
        Client client = ClientBuilder.newClient();
        Response response = client.target(tokenEndpoint)
                .request(MediaType.APPLICATION_FORM_URLENCODED)
                .post(Entity.form(form));
        String json = response.readEntity(String.class);
        response.close();
        client.close();
        return json;
    }

    private TokenResponse parse(String json, String errorPrefix) {
        try {
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {
            });
            if (map.containsKey("access_token")) {
                return TokenResponse.builder()
                        .accessToken(str(map.get("access_token")))
                        .refreshToken(str(map.get("refresh_token")))
                        .tokenType(str(map.get("token_type")))
                        .expiresIn(longVal(map.get("expires_in")))
                        .refreshExpiresIn(longVal(map.get("refresh_expires_in")))
                        .build();
            }
            throw new RuntimeException(errorPrefix + json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String str(Object o) {
        return o == null ? null : o.toString();
    }

    private Long longVal(Object o) {
        try {
            return o == null ? null : Long.parseLong(o.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

