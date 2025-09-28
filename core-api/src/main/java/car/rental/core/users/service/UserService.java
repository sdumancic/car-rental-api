package car.rental.core.users.service;

import car.rental.core.users.domain.model.User;
import car.rental.core.users.domain.repository.UserRepository;
import car.rental.core.users.dto.CreateUserRequest;
import car.rental.core.users.infrastructure.mapper.UserMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User createUser(CreateUserRequest request) {
        User user = UserMapper.toDomain(request);
        return userRepository.save(user);
    }
}
