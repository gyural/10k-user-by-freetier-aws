package org.example.honorsparkingbe.config;

/**
 * Spring Security 설정 클래스
 * - 접근 권한 설정, 로그인 로그아웃 및 세션 관리, CSRF 비활성화 등
 */

import jakarta.servlet.http.HttpServletResponse;
import org.example.honorsparkingbe.security.CustomOAuth2LoginSuccessHandler;
import org.example.honorsparkingbe.security.CustomFormLoginSuccessHandler;
import org.example.honorsparkingbe.security.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import org.springframework.web.cors.CorsConfiguration; // cors
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // cors
import org.springframework.web.filter.CorsFilter; // cors

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableRedisHttpSession
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final String activeProfile;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, Environment environment) {

        this.customOAuth2UserService = customOAuth2UserService;
        this.activeProfile = environment.getProperty("spring.profiles.active", "default"); // 현재 프로필 가져오기
    }

    /**
     * 비밀번호 암호화
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security 관련 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // 접근 설정
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 활성화 -- 250119 추가(이상 시 삭제)
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("api/v1/","/api/v1/auth/login/**", "/api/v1/auth/join", "/confirm").permitAll()
                        .requestMatchers("/api/v1/admin").hasRole("ADMIN")                  // 해당 role만 접근 가능
                        .requestMatchers("/api/v1/my/**").hasAnyRole("ADMIN", "USER") // /api/v1/my/**만 허용
                        .anyRequest().authenticated());

        http
                .formLogin((formLogin) -> formLogin
                        .loginProcessingUrl("/api/v1/auth/login") // 로그인 처리 경로
                        .successHandler(new CustomFormLoginSuccessHandler()) // 커스텀 성공 핸들러 등록(json 반환)
                        .permitAll() // 로그인 페이지 접근 허용
                )

                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/login")
                        //.defaultSuccessUrl("/api/v1/session/info", true) // 소셜 로그인 성공 후 이동 경로
                        .successHandler(new CustomOAuth2LoginSuccessHandler()) // OAuth2 성공 핸들러 등록 (json 반환을 위해)
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService)));


        // CSRF(Cross Site Request Forgery: 사이트 간 요청 위조)
        if ("dev".equals(activeProfile)) {
            http.csrf(auth -> auth.disable()); // 개발 환경에서 CSRF 비활성화
        } else {
            http.csrf(Customizer.withDefaults()); // 프로덕션 환경에서는 CSRF 활성화
        }

        http
                .httpBasic((basic) -> basic.disable());


        // CSRF 활성화 시 로그아웃을 GET 요청으로 하기 위해
//        http
//                .logout((auth) -> auth
//                        .logoutUrl("/api/v1/logout") // 로그아웃 경로
//                        .logoutSuccessUrl("/api/v1/") // 로그아웃 성공 시 리다이렉트 경로
//                );

        // 다중 로그인 설정
//        http
//                .sessionManagement((auth) -> auth
//                        .maximumSessions(1)
//                        .maxSessionsPreventsLogin(true)
//                );

        // 세션 고정 공격 보호
//        http
//                .sessionManagement((auth) -> auth
//                        .sessionFixation().changeSessionId()
//                );


        return http.build();
    }

    /**
     * 🔹 CORS 필터 설정 (프론트엔드 요청 허용)
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 인증 정보를 포함한 요청 허용 (JWT, 세션 쿠키 등)
        config.setAllowedOrigins(List.of("http://localhost:3000")); // 🔹 프론트엔드 주소 허용
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 🔹 허용할 HTTP 메서드 설정
        config.setAllowedHeaders(List.of("*")); // 🔹 모든 요청 헤더 허용
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /**
     * 🔹 CORS 설정을 Security Filter Chain에서 사용할 수 있도록 구성
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000")); // 🔹 프론트엔드 주소 허용
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 🔹 허용할 HTTP 메서드 설정
        config.setAllowedHeaders(List.of("*")); // 🔹 모든 요청 헤더 허용
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
