package car.rental.core.common.domain;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {
    Optional<T> findById(Long id);

    List<T> findAll();

    T save(T entity);

    void deleteById(Long id);
}

