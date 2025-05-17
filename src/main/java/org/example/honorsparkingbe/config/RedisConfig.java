package org.example.honorsparkingbe.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.honorsparkingbe.dto.NotificationQueueItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Value("${spring.data.redis.port}")
  public int port;

  @Value("${spring.data.redis.host}")
  public String host;

  @Bean
  @Primary // 250514
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
  }


@Bean
@Primary
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
  RedisTemplate<String, Object> template = new RedisTemplate<>();
  template.setConnectionFactory(redisConnectionFactory);

  // Key 및 해시 키 직렬화기는 String 기반으로
  template.setKeySerializer(new StringRedisSerializer());
  template.setHashKeySerializer(new StringRedisSerializer());

  // 값 및 해시 값 직렬화기는 JDK 직렬화로
  JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();
  template.setValueSerializer(jdkSerializer);
  template.setHashValueSerializer(jdkSerializer);

  return template;
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
}
