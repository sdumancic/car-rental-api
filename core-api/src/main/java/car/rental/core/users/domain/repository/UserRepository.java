package car.rental.core.users.domain.repository;

import car.rental.core.users.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    List<User> findAll();

    User save(User user);

    void deleteById(Long id);

    User update(User user);

    void softDeleteById(Long id);
}
