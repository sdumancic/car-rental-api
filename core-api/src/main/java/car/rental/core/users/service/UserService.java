package car.rental.core.users.service;

import car.rental.core.azure.dto.UploadResult;
import car.rental.core.azure.service.AzureBlobService;
import car.rental.core.common.dto.PageResponse;
import car.rental.core.common.exception.ResourceNotFoundException;
import car.rental.core.users.domain.model.User;
import car.rental.core.users.domain.repository.UserRepository;
import car.rental.core.users.dto.CreateUserRequest;
import car.rental.core.users.dto.LoginUserRequest;
import car.rental.core.users.dto.QueryUserRequest;
import car.rental.core.users.dto.TokenResponse;
import car.rental.core.users.infrastructure.mapper.UserMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AzureBlobService azureBlobService;
    private final KeycloakUserService keycloakUserService; // still used for user creation
    private final KeycloakTokenService keycloakTokenService; // new token handling service
    private final RefreshTokenStore refreshTokenStore; // Redis-backed refresh token storage

    @Transactional
    public User createUser(CreateUserRequest request) {
        // Create user in Keycloak first
        String keycloakId = keycloakUserService.createUserInKeycloak(request);
        // Then create user in database
        User user = UserMapper.toDomain(request);
        user.setKeycloakId(keycloakId);
        return userRepository.save(user);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User updateUser(Long id, CreateUserRequest request) {
        User user = UserMapper.toDomain(request);
        user.setId(id);
        return userRepository.update(user);
    }

    @Transactional
    public void softDeleteUser(Long id) {
        User existing = findUserById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.softDeleteById(id);
        // Optionally remove refresh token
        if (existing.getUsername() != null) {
            refreshTokenStore.delete(existing.getUsername());
        }
    }

    public PageResponse<User> findUsers(QueryUserRequest query, UriInfo uriInfo) {
        return userRepository.findUsers(query, uriInfo);
    }

    @Transactional
    public UploadResult uploadDriverLicense(Long userId, java.io.InputStream fileInput, String fileName) {
        User user = findUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        UploadResult result = azureBlobService.uploadDriverLicense(fileInput, fileName, userId);
        // Remove any leading userId/ from blobName before storing
        String blobName = result.getBlobName();
        if (blobName.startsWith(userId + "/")) {
            blobName = blobName.substring((userId + "/").length());
        }
        userRepository.updateDriverLicenseBlobId(userId, blobName); // Only update the blobId
        return result;
    }

    public byte[] downloadDriverLicense(Long userId) {
        User user = findUserById(userId);
        if (user == null || user.getDriverLicenseBlobId() == null) {
            throw new ResourceNotFoundException("Driver license not found");
        }
        return azureBlobService.downloadDriverLicense(user.getDriverLicenseBlobId(), userId);
    }

    public String generateDriverLicenseDownloadLink(Long userId) {
        User user = findUserById(userId);
        if (user == null || user.getDriverLicenseBlobId() == null) {
            throw new ResourceNotFoundException("Driver license not found");
        }
        return azureBlobService.generateDriverLicenseDownloadLink(userId, user.getDriverLicenseBlobId());
    }

    // New login returning tokens and persisting refresh token in Redis with TTL
    public TokenResponse login(@Valid LoginUserRequest request) {
        TokenResponse tokens = keycloakTokenService.login(request);
        log.info("tokens: {}", tokens);
        if (tokens.getRefreshToken() != null) {
            log.info("refresh token: {} {} {}", tokens.getRefreshToken(), tokens.getRefreshExpiresIn(), request.getUsername());
            refreshTokenStore.save(request.getUsername(), tokens.getRefreshToken(), tokens.getRefreshExpiresIn());
        }
        return tokens;
    }

    // Legacy access-token-only helper for existing endpoints expecting a String
    public String loginAccessToken(@Valid LoginUserRequest request) {
        return login(request).getAccessToken();
    }

    // Refresh flow: retrieve refresh token from Redis by username and call Keycloak refresh endpoint
    public TokenResponse refresh(String username) {
        var stored = refreshTokenStore.get(username)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found or expired"));
        TokenResponse tokens = keycloakTokenService.refresh(stored);
        // Update stored refresh token (rotation) with new TTL
        if (tokens.getRefreshToken() != null) {
            refreshTokenStore.save(username, tokens.getRefreshToken(), tokens.getRefreshExpiresIn());
        }
        return tokens;
    }
}
