package org.example.honorsparkingbe.config;

/**
 * Spring Security 설정 클래스 - 접근 권한 설정, 로그인 로그아웃 및 세션 관리, CSRF 비활성화 등
 */

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;
import org.example.honorsparkingbe.security.ApiKeyAuthFilter;
import org.example.honorsparkingbe.security.AuthWhiteList;
import org.example.honorsparkingbe.security.CustomFormLoginSuccessHandler;
import org.example.honorsparkingbe.security.CustomOAuth2LoginSuccessHandler;
import org.example.honorsparkingbe.security.CustomOAuth2UserService;
import org.example.honorsparkingbe.security.RequestLoggingFilter;
import org.example.honorsparkingbe.util.AesUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@EnableRedisHttpSession
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;
  private final String activeProfile;
  private final ApiKeyAuthFilter apiKeyAuthFilter;
  private final AesUtil aesUtil;
  private final RequestLoggingFilter requestLoggingFilter;

  private static final Set<String> CSRF_REQUIRED_PROFILES = Set.of("prod", "performanceTest");

  public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, Environment environment,
      ApiKeyAuthFilter apiKeyAuthFilter, AesUtil aesUtil,
      RequestLoggingFilter requestLoggingFilter) {

    this.customOAuth2UserService = customOAuth2UserService;
    this.activeProfile = environment.getProperty("spring.profiles.active",
        "default"); // 현재 프로필 가져오기
    this.apiKeyAuthFilter = apiKeyAuthFilter;
    this.aesUtil = aesUtil;
    this.requestLoggingFilter = requestLoggingFilter;
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
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // 접근 설정
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 활성화
        .authorizeHttpRequests((auth) -> auth
            .requestMatchers(AuthWhiteList.PERMIT_ALL_PATHS).permitAll()
            .requestMatchers("/api/v1/admin").hasRole("ADMIN")                  // 해당 role만 접근 가능
            .requestMatchers("/api/v1/my/**").hasAnyRole("ADMIN", "USER") // /api/v1/my/**만 허용
            .anyRequest().authenticated()
        )
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint((request, response, authException) -> {
              response.setContentType("application/json;charset=UTF-8");
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.getWriter().write("{\"error\":\"Invalid or missing authentication\"}");
            })
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              response.setContentType("application/json;charset=UTF-8");
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.getWriter().write("{\"error\":\"Unauthorized - Access is denied\"}");
            })
        )
        .addFilterBefore(apiKeyAuthFilter,
            UsernamePasswordAuthenticationFilter.class); // API Key 필터 추가

    http
//        .addFilterBefore(requestLoggingFilter, CorsFilter.class)
        .formLogin((formLogin) -> formLogin
            .loginProcessingUrl("/api/v1/auth/login") // 로그인 처리 경로
            .successHandler(new CustomFormLoginSuccessHandler()) // 커스텀 성공 핸들러 등록(json 반환)
            .failureHandler((request, response, exception) -> {
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.setContentType("application/json;charset=UTF-8");
              response.getWriter().write("{\"error\":\"Invalid username or password\"}");
              System.out.println("🚨 Login failed: " + exception.getMessage());
            })
            .permitAll() // 로그인 페이지 접근 허용
        )
        .oauth2Login((oauth2) -> oauth2
            .loginPage("/login")
            //.defaultSuccessUrl("/api/v1/session/info", true) // 소셜 로그인 성공 후 이동 경로
            .successHandler(
                new CustomOAuth2LoginSuccessHandler(aesUtil)) // OAuth2 성공 핸들러 등록 (json 반환을 위해)
            .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                .userService(customOAuth2UserService)));

    // CSRF(Cross Site Request Forgery: 사이트 간 요청 위조)
    if (!CSRF_REQUIRED_PROFILES.contains(activeProfile)) {
      http.csrf(auth -> auth.disable());
    } else {
      http.csrf(csrf -> csrf
          .ignoringRequestMatchers("/api/v1/auth/custom-session-login")
          .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
      );
    }

    http
        .httpBasic((basic) -> basic.disable());

    return http.build();
  }

  /**
   * CORS 설정을 Security Filter Chain에서 사용할 수 있도록 구성
   */
  @Bean
  public UrlBasedCorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowCredentials(true);
    config.setAllowedOriginPatterns(List.of( // 프론트엔드 도메인 허용
        "http://localhost:3000",
        "https://honorsparking-web.vercel.app"
    ));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드 설정
    config.setAllowedHeaders(List.of("*")); // 모든 요청 헤더 허용
    config.setExposedHeaders(List.of("Authorization", "Set-Cookie", "X-API-KEY"));

    source.registerCorsConfiguration("/**", config);
    return source;
  }

  /**
   * 쿠키를 크로스 사이트 요청에서도 사용할 수 있도록 SameSite=None; Secure 설정 추가
   */
  @Bean
  public CookieSerializer cookieSerializer() {
    DefaultCookieSerializer serializer = new DefaultCookieSerializer();
    serializer.setCookieName("SESSION"); // 세션 쿠키 이름
    serializer.setCookiePath("/");
    serializer.setUseSecureCookie(true); // HTTPS에서만 쿠키 전송 (http에서는 쿠키 전송이 안되므로 개발 환경에서는 false로 설정)
    serializer.setSameSite("None"); // 크로스 사이트 요청에서 쿠키 허용
    return serializer;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }
}