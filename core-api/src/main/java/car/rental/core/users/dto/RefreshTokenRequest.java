package car.rental.core.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank
    private String username; // identify whose refresh token to use from Redis
}

