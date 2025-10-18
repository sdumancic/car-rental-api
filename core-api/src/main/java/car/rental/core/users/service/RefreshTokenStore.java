package car.rental.core.users.service;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@ApplicationScoped
public class RefreshTokenStore {

    @Inject
    RedisDataSource redisDataSource;

    private ValueCommands<String, String> valueCommands;

    private KeyCommands<String> keyCommands;

    @PostConstruct
    void init() {
        this.valueCommands = redisDataSource.value(String.class);
        this.keyCommands = redisDataSource.key();
    }

    private String key(String username) {
        return "refresh:" + username;
    }

    public void save(String username, String refreshToken, Long ttlSeconds) {
        log.info("Saving refresh token for user '{}', ttlSeconds={}", username, ttlSeconds);
        if (refreshToken == null || username == null || username.isBlank()) return;
        if (ttlSeconds == null || ttlSeconds <= 0) {
            valueCommands.set(key(username), refreshToken);
        } else {
            valueCommands.setex(key(username), ttlSeconds, refreshToken);
        }
    }

    public Optional<String> get(String username) {
        if (username == null || username.isBlank()) return Optional.empty();
        return Optional.ofNullable(valueCommands.get(key(username)));
    }

    public void delete(String username) {
        if (username == null || username.isBlank()) return;
        keyCommands.del(key(username));
    }
}
