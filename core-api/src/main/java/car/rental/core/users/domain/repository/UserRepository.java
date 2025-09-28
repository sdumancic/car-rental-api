package car.rental.core.users.domain.repository;

import car.rental.core.users.domain.model.User;

import java.util.Optional;
import java.util.List;

public interface UserRepository {

    Optional<User> findById(Long id);

    List<User> findAll();

    User save(User user);

    void deleteById(Long id);
}
