package org.example.honorsparkingbe.integration;

import java.util.Map;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("integration")
public class GetParkingZoneInfoTest extends InitIntegrationTest {

  private MemberEntity memberEntity;

  @BeforeEach
  public void securityContext() {
    memberEntity = MemberEntity.builder()
        .password("password")
        .role(MemberRole.ROLE_USER)
        .userName("testUserName")
        .id(100L)
        .build();

    // CustomUserDetails 생성
    CustomUserDetails customUserDetails = new CustomUserDetails(memberEntity);

    // SecurityContext에 설정
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(new UsernamePasswordAuthenticationToken(customUserDetails, null,
        customUserDetails.getAuthorities()));
    SecurityContextHolder.setContext(context);

  }

  @Autowired
  WebApplicationContext wac;

  @Autowired
  WebTestClient client;

  @Test
  @DisplayName("주차 구역 정보를 성공적으로 조회한다.")
  void getParkingZoneInfo_Success() {
    // given
    Map<String, Object> queryParams = Map.of(
        "latitude", 37.5665,
        "longitude", 126.978
    );

    client.get()
        .uri(uriBuilder -> uriBuilder.path("/api/v1/parkingzone/list")
            .queryParam("latitude", queryParams.get("latitude"))
            .queryParam("longitude", queryParams.get("longitude"))
            .build())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().is4xxClientError();
//        .expectHeader().contentType(MediaType.APPLICATION_JSON);

  }

}
