package car.rental.core.billingdetails.infrastructure.mapper;

import car.rental.core.billingdetails.domain.model.BillingDetails;
import car.rental.core.billingdetails.dto.CreateBillingDetailsRequest;
import car.rental.core.billingdetails.infrastructure.BillingDetailsEntity;
import car.rental.core.users.domain.model.User;
import car.rental.core.users.infrastructure.mapper.UserMapper;

import java.time.Instant;

public class BillingDetailsMapper {
    // --- API → Domain ---
    public static BillingDetails toDomain(CreateBillingDetailsRequest request, User user) {
        if (request == null) {
            return null;
        }
        return BillingDetails.builder()
                .id(null)
                .cardNumber(request.getCardNumber())
                .cardHolder(request.getCardHolder())
                .expiryDate(request.getExpiryDate())
                .billingAddress(request.getBillingAddress())
                .provider(request.getProvider())
                .active(true)
                .user(user)
                .dateCreated(Instant.now())
                .dateModified(Instant.now())
                .build();
    }

    // --- Domain → Entity ---
    public static BillingDetailsEntity toEntity(BillingDetails domain) {
        if (domain == null) {
            return null;
        }
        BillingDetailsEntity entity = new BillingDetailsEntity();
        entity.setId(domain.getId());
        entity.setCardNumber(domain.getCardNumber());
        entity.setCardHolder(domain.getCardHolder());
        entity.setExpiryDate(domain.getExpiryDate());
        entity.setBillingAddress(domain.getBillingAddress());
        entity.setProvider(domain.getProvider());
        entity.setActive(domain.getActive());
        entity.setUser(UserMapper.toEntity(domain.getUser()));
        entity.setDateCreated(domain.getDateCreated());
        entity.setDateModified(domain.getDateModified());
        return entity;
    }

    // --- Entity → Domain ---
    public static BillingDetails toDomain(BillingDetailsEntity entity) {
        if (entity == null) {
            return null;
        }
        return BillingDetails.builder()
                .id(entity.getId())
                .cardNumber(entity.getCardNumber())
                .cardHolder(entity.getCardHolder())
                .expiryDate(entity.getExpiryDate())
                .billingAddress(entity.getBillingAddress())
                .provider(entity.getProvider())
                .active(entity.getActive())
                .user(UserMapper.toDomain(entity.getUser()))
                .dateCreated(entity.getDateCreated())
                .dateModified(entity.getDateModified())
                .build();
    }

    public static void updateEntity(BillingDetailsEntity entity, BillingDetails domain) {
        if (entity == null || domain == null) {
            return;
        }
        entity.setCardNumber(domain.getCardNumber());
        entity.setCardHolder(domain.getCardHolder());
        entity.setExpiryDate(domain.getExpiryDate());
        entity.setBillingAddress(domain.getBillingAddress());
        entity.setProvider(domain.getProvider());
        entity.setActive(domain.getActive());
        entity.setUser(UserMapper.toEntity(domain.getUser()));
        entity.setDateModified(Instant.now());
    }
}
