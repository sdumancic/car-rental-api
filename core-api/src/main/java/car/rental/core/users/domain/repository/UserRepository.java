package car.rental.core.users.domain.repository;

import car.rental.core.common.dto.PageResponse;
import car.rental.core.users.domain.model.User;
import car.rental.core.users.dto.QueryUserRequest;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    User save(User user);

    void deleteById(Long id);

    User update(User user);

    void softDeleteById(Long id);

    PageResponse<User> findUsers(QueryUserRequest query, UriInfo uriInfo);

    void updateDriverLicenseBlobId(Long userId, String blobId);
}
