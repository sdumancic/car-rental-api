package car.rental.core.users.infrastructure.persistence;

import car.rental.core.common.dto.PageResponse;
import car.rental.core.common.exception.ResourceNotFoundException;
import car.rental.core.users.domain.model.User;
import car.rental.core.users.domain.repository.UserRepository;
import car.rental.core.users.dto.QueryUserRequest;
import car.rental.core.users.infrastructure.mapper.UserMapper;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class PanacheUserRepository implements UserRepository {

    private final UserEntityRepository entityRepo;

    @Override
    public Optional<User> findById(Long id) {
        return entityRepo.findByIdOptional(id).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return entityRepo.find("username", username).firstResultOptional()
                .map(UserMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return entityRepo.listAll().stream().map(UserMapper::toDomain).toList();
    }

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        entityRepo.persist(entity);
        return UserMapper.toDomain(entity);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {

        UserEntity entity = entityRepo.findById(id);
        if (entity != null) {
            entityRepo.delete(entity);
        }
    }

    @Transactional
    @Override
    public User update(User user) {
        // Hybrid approach: fetch managed entity first, then update fields
        UserEntity managed = entityRepo.findById(user.getId());
        if (managed == null) {
            throw new ResourceNotFoundException("User not found: " + user.getId());
        }
        UserMapper.updateEntity(managed, user); // copies domain state → managed entity
        return UserMapper.toDomain(managed);
    }

    @Transactional
    @Override
    public void softDeleteById(Long id) {
        UserEntity entity = entityRepo.findById(id);
        if (entity != null) {
            entity.setActive(false);
            entityRepo.persist(entity);
        }
    }

    @Override
    public PageResponse<User> findUsers(QueryUserRequest query, UriInfo uriInfo) {
        StringBuilder queryStr = new StringBuilder("active = true");
        List<Object> params = new ArrayList<>();

        // Filter by name (firstName or lastName contains)
        if (query.getName() != null && !query.getName().isEmpty()) {
            queryStr.append(" and (lower(firstName) like ?1 or lower(lastName) like ?1)");
            params.add("%" + query.getName().toLowerCase() + "%");
        }

        // Filter by email
        if (query.getEmail() != null && !query.getEmail().isEmpty()) {
            queryStr.append(" and lower(email) like ?").append(params.size() + 1);
            params.add("%" + query.getEmail().toLowerCase() + "%");
        }

        // Sort
        Sort sort = Sort.by("id"); // default
        if (query.getSort() != null && !query.getSort().isEmpty()) {
            if ("name".equalsIgnoreCase(query.getSort())) {
                sort = Sort.by("firstName");
            } else if ("email".equalsIgnoreCase(query.getSort())) {
                sort = Sort.by("email");
            }
        }

        // Query
        var panacheQuery = entityRepo.find(queryStr.toString(), sort, params.toArray());
        long totalElements = panacheQuery.count();

        List<UserEntity> entities = panacheQuery.page(query.getPage(), query.getSize()).list();
        List<User> users = entities.stream().map(UserMapper::toDomain).collect(Collectors.toList());

        // Build URLs
        UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());
        String currentPageUrl = uriBuilder.build().toString();

        String prevPageUrl = null;
        if (query.getPage() > 0) {
            prevPageUrl = uriBuilder.queryParam("page", query.getPage() - 1).build().toString();
        }

        String nextPageUrl = null;
        long totalPages = (long) Math.ceil((double) totalElements / query.getSize());
        if (query.getPage() + 1 < totalPages) {
            nextPageUrl = uriBuilder.queryParam("page", query.getPage() + 1).build().toString();
        }

        PageResponse.PageMetadata metadata = PageResponse.PageMetadata.builder()
                .page(query.getPage())
                .size(query.getSize())
                .totalRecords(totalElements)
                .currentPageUrl(currentPageUrl)
                .prevPageUrl(prevPageUrl)
                .nextPageUrl(nextPageUrl)
                .build();

        return PageResponse.<User>builder().data(users).metadata(metadata).build();
    }

    @Transactional
    @Override
    public void updateDriverLicenseBlobId(Long userId, String blobId) {
        UserEntity entity = entityRepo.findById(userId);
        if (entity != null) {
            entity.setDriverLicenseBlobId(blobId);
            entityRepo.persist(entity);
        }
    }
}
