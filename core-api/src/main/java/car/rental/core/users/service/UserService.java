package car.rental.core.users.service;

import car.rental.core.common.util.HashUtils;
import car.rental.core.users.domain.model.Address;
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

    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User updateUser(Long id, CreateUserRequest request) {
        User existing = findUserById(id);
        if (existing == null) {
            throw new RuntimeException("User not found");
        }
        User updated = User.builder()
                .id(id)
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .mobileNumber(request.getMobileNumber())
                .passwordHash(HashUtils.hashPassword(request.getPassword()))
                .homeAddress(Address.builder()
                        .street(request.getHomeStreet())
                        .houseNumber(request.getHomeHouseNumber())
                        .zipcode(request.getHomeZipcode())
                        .city(request.getHomeCity())
                        .build())
                .billingAddress(Address.builder()
                        .street(request.getBillingStreet())
                        .houseNumber(request.getBillingHouseNumber())
                        .zipcode(request.getBillingZipcode())
                        .city(request.getBillingCity())
                        .build())
                .active(existing.getActive())
                .build();
        return userRepository.update(updated);
    }

    @Transactional
    public void softDeleteUser(Long id) {
        userRepository.softDeleteById(id);
    }
}
