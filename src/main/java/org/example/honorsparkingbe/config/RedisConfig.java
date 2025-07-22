package org.example.honorsparkingbe.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.NotificationQueueItem;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

  private final RedisProperties redisProperties;


  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
    standaloneConfig.setHostName(redisProperties.getHost());
    standaloneConfig.setPort(redisProperties.getPort());
    standaloneConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));

    LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
        .build();

    return new LettuceConnectionFactory(standaloneConfig, clientConfig);
  }


  @Bean
  public RedisTemplate<String, NotificationQueueItem> notificationRedisTemplate(
      RedisConnectionFactory redisConnectionFactory
  ) {
    // ObjectMapper 직접 생성 (Bean으로 등록 X)
    BasicPolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
        .allowIfSubType(Object.class)
        .build();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.activateDefaultTyping(validator, ObjectMapper.DefaultTyping.NON_FINAL);

    RedisTemplate<String, NotificationQueueItem> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());

    // 커스텀 ObjectMapper를 사용하는 Serializer 설정
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

    return template;
  }

  /**
   * Configures and returns a RedisTemplate for String keys and generic Object values using JDK serialization.
   *
   * The template uses UTF-8 string serialization for keys, JDK serialization for values and hash values, and a generic to-string serializer for hash keys.
   *
   * @return a RedisTemplate configured for String keys and Object values with appropriate serializers
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
    redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
    redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
    redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    return redisTemplate;
  }
}
