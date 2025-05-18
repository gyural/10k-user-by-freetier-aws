package org.example.honorsparkingbe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisSessionManualConfig {

    @Bean
    public RedisIndexedSessionRepository redisIndexedSessionRepository(
            RedisTemplate<String, Object> redisTemplate) {
        RedisIndexedSessionRepository repository = new RedisIndexedSessionRepository(redisTemplate);
        repository.setDefaultSerializer(new JdkSerializationRedisSerializer());
        return repository;
    }
}



