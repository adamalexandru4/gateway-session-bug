package ro.adam.gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.config.ReactiveSessionRepositoryCustomizer;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

@Configuration
@EnableRedisWebSession
@RequiredArgsConstructor
public class SessionConfig {
    final SessionProperties sessionProperties;

    @Bean
    public ReactiveSessionRepositoryCustomizer<ReactiveRedisSessionRepository> reactiveSessionRepositoryCustomizer() {
        return sessionRepository -> sessionRepository
                .setDefaultMaxInactiveInterval((int) sessionProperties.getTimeout().toSeconds());
    }
}
