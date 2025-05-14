package org.example.honorsparkingbe.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.data.redis.core.RedisTemplate;

//@Configuration
//public class RedisSessionManualConfig {
//
//    @Bean
//    public RedisIndexedSessionRepository redisIndexedSessionRepository(
//            RedisTemplate<String, Object> redisTemplate) {
//
//        RedisIndexedSessionRepository repository = new RedisIndexedSessionRepository(redisTemplate);
//
//        // 커스텀 ObjectMapper를 직접 만든 RedisSerializer를 사용
//        BasicPolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
//                .allowIfSubType(Object.class)
//                .build();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.activateDefaultTyping(validator, ObjectMapper.DefaultTyping.NON_FINAL);
//
//        GenericJackson2JsonRedisSerializer customSerializer =
//                new GenericJackson2JsonRedisSerializer(objectMapper);
//
//        repository.setDefaultSerializer(customSerializer); // ✅ 핵심
//
//        return repository;
//    }
//}

@Configuration
public class RedisSessionManualConfig {

    @Bean
    public RedisIndexedSessionRepository redisIndexedSessionRepository(
            RedisTemplate<String, Object> redisTemplate) {
        RedisIndexedSessionRepository repository = new RedisIndexedSessionRepository(redisTemplate);
        repository.setDefaultSerializer(new JdkSerializationRedisSerializer()); // ✅ 핵심
        return repository;
    }
}



