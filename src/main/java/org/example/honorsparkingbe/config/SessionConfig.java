//package org.example.honorsparkingbe.config;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
//import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializer;
//
//@Configuration
//@EnableRedisHttpSession
//public class SessionConfig {
//
//    @Bean
//    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
//        return new JdkSerializationRedisSerializer(); // ✅ Spring Session 전용 JDK 직렬화
//    }
//}
//
