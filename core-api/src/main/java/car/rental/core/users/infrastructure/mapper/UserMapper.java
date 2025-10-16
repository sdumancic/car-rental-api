package car.rental.core.users.infrastructure.mapper;

import car.rental.core.common.util.HashUtils;
import car.rental.core.users.domain.model.Address;
import car.rental.core.users.domain.model.User;
import car.rental.core.users.dto.CreateUserRequest;
import car.rental.core.users.infrastructure.persistence.AddressEmbeddable;
import car.rental.core.users.infrastructure.persistence.UserEntity;

public class UserMapper {
    // --- API → Domain ---
    public static User toDomain(CreateUserRequest request) {
        if (request == null) {
            return null;
        }
        Address homeAddress = Address.builder().
                street(request.getHomeStreet()).
                houseNumber(request.getHomeHouseNumber()).
                zipcode(request.getHomeZipcode()).
                city(request.getHomeCity())
                .build();
        Address billingAddress = Address.builder().
                street(request.getBillingStreet()).
                houseNumber(request.getBillingHouseNumber()).
                zipcode(request.getBillingZipcode()).
                city(request.getBillingCity())
                .build();

        return User.builder()
                .id(null)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .mobileNumber(request.getMobileNumber())
                .passwordHash(HashUtils.hashPassword(request.getPassword()))
                .homeAddress(homeAddress)
                .billingAddress(billingAddress)
                .active(true)
                .build();
    }


    // --- Domain → Entity ---
    public static UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setPhoneNumber(domain.getPhoneNumber());
        entity.setMobileNumber(domain.getMobileNumber());
        entity.setDateCreated(domain.getDateCreated());
        entity.setDateModified(domain.getDateModified());
        if (domain.getHomeAddress() != null) {
            AddressEmbeddable homeAddress = new AddressEmbeddable();
            homeAddress.setStreet(domain.getHomeAddress().getStreet());
            homeAddress.setHouseNumber(domain.getHomeAddress().getHouseNumber());
            homeAddress.setZipcode(domain.getHomeAddress().getZipcode());
            homeAddress.setCity(domain.getHomeAddress().getCity());
            entity.setHomeAddress(homeAddress);
        }
        if (domain.getBillingAddress() != null) {
            AddressEmbeddable billingAddress = new AddressEmbeddable();
            billingAddress.setStreet(domain.getBillingAddress().getStreet());
            billingAddress.setHouseNumber(domain.getBillingAddress().getHouseNumber());
            billingAddress.setZipcode(domain.getBillingAddress().getZipcode());
            billingAddress.setCity(domain.getBillingAddress().getCity());
            entity.setBillingAddress(billingAddress);
        }
        entity.setActive(domain.getActive());
        entity.setDriverLicenseBlobId(domain.getDriverLicenseBlobId());
        entity.setKeycloakId(domain.getKeycloakId());
        return entity;
    }

    // --- Entity → Domain ---
    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        var homeAddress = entity.getHomeAddress() != null
                ? Address.builder()
                .street(entity.getHomeAddress().getStreet())
                .houseNumber(entity.getHomeAddress().getHouseNumber())
                .zipcode(entity.getHomeAddress().getZipcode())
                .city(entity.getHomeAddress().getCity())
                .build()
                : null;

        var billingAddress = entity.getBillingAddress() != null
                ? Address.builder()
                .street(entity.getBillingAddress().getStreet())
                .houseNumber(entity.getBillingAddress().getHouseNumber())
                .zipcode(entity.getBillingAddress().getZipcode())
                .city(entity.getBillingAddress().getCity())
                .build()
                : null;

        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .dateCreated(entity.getDateCreated())
                .dateModified(entity.getDateModified())
                .homeAddress(homeAddress)
                .billingAddress(billingAddress)
                .phoneNumber(entity.getPhoneNumber())
                .mobileNumber(entity.getMobileNumber())
                .driverLicenseBlobId(entity.getDriverLicenseBlobId())
                .active(entity.getActive())
                .keycloakId(entity.getKeycloakId())
                .build();
    }

    public static void updateEntity(UserEntity entity, User domain) {
        entity.setUsername(domain.getUsername());
        entity.setEmail(domain.getEmail());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setPhoneNumber(domain.getPhoneNumber());
        entity.setMobileNumber(domain.getMobileNumber());
        entity.setDateModified(domain.getDateModified());
        if (domain.getPasswordHash() != null) {
            entity.setPasswordHash(domain.getPasswordHash());
        }
        if (domain.getHomeAddress() != null) {
            AddressEmbeddable homeAddress = entity.getHomeAddress();
            if (homeAddress == null) {
                homeAddress = new AddressEmbeddable();
            }
            homeAddress.setStreet(domain.getHomeAddress().getStreet());
            homeAddress.setHouseNumber(domain.getHomeAddress().getHouseNumber());
            homeAddress.setZipcode(domain.getHomeAddress().getZipcode());
            homeAddress.setCity(domain.getHomeAddress().getCity());
            entity.setHomeAddress(homeAddress);
        }
        if (domain.getBillingAddress() != null) {
            AddressEmbeddable billingAddress = entity.getBillingAddress();
            if (billingAddress == null) {
                billingAddress = new AddressEmbeddable();
            }
            billingAddress.setStreet(domain.getBillingAddress().getStreet());
            billingAddress.setHouseNumber(domain.getBillingAddress().getHouseNumber());
            billingAddress.setZipcode(domain.getBillingAddress().getZipcode());
            billingAddress.setCity(domain.getBillingAddress().getCity());
            entity.setBillingAddress(billingAddress);
        }
        entity.setActive(domain.getActive());
        entity.setDriverLicenseBlobId(domain.getDriverLicenseBlobId());
    }

}


