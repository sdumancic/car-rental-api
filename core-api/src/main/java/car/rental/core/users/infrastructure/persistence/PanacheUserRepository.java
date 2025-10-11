package car.rental.core.users.infrastructure.persistence;

import car.rental.core.users.domain.model.User;
import car.rental.core.users.domain.repository.UserRepository;
import car.rental.core.users.infrastructure.mapper.UserMapper;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;


@ApplicationScoped
@RequiredArgsConstructor
public class PanacheUserRepository implements UserRepository {

    private final UserEntityRepository entityRepo;

    @Override
    public Optional<User> findById(Long id) {
        return entityRepo.findByIdOptional(id).map(UserMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return entityRepo.listAll().stream().map(UserMapper::toDomain).toList();
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        entityRepo.persist(entity);
        return UserMapper.toDomain(entity);
    }

    @Override
    public void deleteById(Long id) {
        entityRepo.deleteById(id);
    }

    @Override
    public User update(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        entityRepo.persist(entity);
        return UserMapper.toDomain(entity);
    }

    @Override
    public void softDeleteById(Long id) {
        UserEntity entity = entityRepo.findById(id);
        if (entity != null) {
            entity.setActive(false);
            entityRepo.persist(entity);
        }
    }
}
