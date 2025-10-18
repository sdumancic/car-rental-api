package car.rental.core.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType; // bearer
    private Long expiresIn;   // access token seconds
    private Long refreshExpiresIn; // refresh token seconds
}
