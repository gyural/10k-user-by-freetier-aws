package org.example.honorsparkingbe.unit.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.catalina.security.SecurityConfig;
import org.example.honorsparkingbe.Slf4jRestControllerAdvice;
import org.example.honorsparkingbe.controller.ParkingZoneSearchController;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.dto.GetParkingZoneByKeywordDTO;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.service.ParkingZoneSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@WebMvcTest
@ContextConfiguration(classes = SecurityConfig.class)
public class ParkingZoneSearchControllerTest {

  @InjectMocks
  ParkingZoneSearchController parkingZoneSearchController;
  @Mock
  ParkingZoneSearchService parkingZoneSearchService;
  @Mock
  private MemberEntity memberEntity;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
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

    // mockMvc설정
    mockMvc = MockMvcBuilders
        .standaloneSetup(parkingZoneSearchController)
        .setControllerAdvice(new Slf4jRestControllerAdvice()) // 예외 핸들러 등록
        .setCustomArgumentResolvers() // 필요하면 여기에 추가
        .build();
  }

  @Test
  @DisplayName("올바른 값이 들어왔을 때 올바른 키워드 검색 응답값이 오는지 확인")
  void getParkingZoneSearchByRightRequest() throws Exception {

    //Given
    String targetKeyword = "서울 강남";

    //When
    mockMvc.perform(get("/api/v1/search/parking").queryParam("keyword", targetKeyword))
        .andExpect(status().isOk());

    GetParkingZoneByKeywordDTO expectedDTO = GetParkingZoneByKeywordDTO.builder()
        .keyword(targetKeyword)
        .build();

    //Then
    verify(parkingZoneSearchService, times(1)).getParkingZonesByKeyword(eq(expectedDTO));
  }

  @Test
  @DisplayName("필수 쿼리값이 안들어갔을 때 400에러 확인 - 디버깅용 전체 응답 출력")
  void getParkingZoneSearchByWrongRequest_DebugPrint() throws Exception {
    // Given
    String expectErrMsg = "keyword 쿼리 파라매터는 필수입니다.";

    // When
    mockMvc.perform(get("/api/v1/search/parking"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.message").value(expectErrMsg));

  }


}
