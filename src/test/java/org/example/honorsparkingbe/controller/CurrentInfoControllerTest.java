package org.example.honorsparkingbe.controller;

import org.example.honorsparkingbe.dto.CustomOAuth2User;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.service.CurrentInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CurrentInfoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CurrentInfoService currentInfoService;

    @InjectMocks
    private CurrentInfoController currentInfoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(currentInfoController).build();
    }

    @Test
    void testGetParkingInfo_NoParkingHistory() throws Exception {
        // 가짜 사용자 인증 정보 설정
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 사용자 정보 모의 (CustomUserDetails 사용)
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // 서비스 응답 모의 (주차 기록 없음)
        when(currentInfoService.getCurrentParkingInfo(1L)).thenReturn(Map.of("message", "해당 사용자의 주차 기록이 없습니다."));

        // API 호출 및 응답 확인
        mockMvc.perform(get("/api/v1/parking/me").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("해당 사용자의 주차 기록이 없습니다."));

        verify(currentInfoService, times(1)).getCurrentParkingInfo(1L);
    }

    @Test
    void testGetParkingInfo_NotParked() throws Exception {
        // 가짜 사용자 인증 정보 설정
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 사용자 정보 모의 (OAuth2User 사용)
        CustomOAuth2User oauthUser = mock(CustomOAuth2User.class);
        when(oauthUser.getId()).thenReturn(2L);
        when(authentication.getPrincipal()).thenReturn(oauthUser);

        // 서비스 응답 모의 (현재 주차 중 아님)
        when(currentInfoService.getCurrentParkingInfo(2L)).thenReturn(Map.of(
                "isParked", false,
                "message", "현재 주차 중인 상태가 아닙니다."
        ));

        // API 호출 및 응답 확인
        mockMvc.perform(get("/api/v1/parking/me").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isParked").value(false))
                .andExpect(jsonPath("$.message").value("현재 주차 중인 상태가 아닙니다."));

        verify(currentInfoService, times(1)).getCurrentParkingInfo(2L);
    }

    @Test
    void testGetParkingInfo_CurrentlyParked() throws Exception {
        // 가짜 사용자 인증 정보 설정
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 사용자 정보 모의 (CustomUserDetails 사용)
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(3L);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // 서비스 응답 모의 (현재 주차 중)
        when(currentInfoService.getCurrentParkingInfo(3L)).thenReturn(Map.of(
                "parkingZone", Map.of(
                        "zoneName", "A구역",
                        "hourlyRate", 2000,
                        "entranceTime", "2024-02-19T12:00:00",
                        "cost", 4000
                )
        ));

        // API 호출 및 응답 확인
        mockMvc.perform(get("/api/v1/parking/me").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parkingZone.zoneName").value("A구역"))
                .andExpect(jsonPath("$.parkingZone.hourlyRate").value(2000))
                .andExpect(jsonPath("$.parkingZone.entranceTime").value("2024-02-19T12:00:00"))
                .andExpect(jsonPath("$.parkingZone.cost").value(4000));

        verify(currentInfoService, times(1)).getCurrentParkingInfo(3L);
    }
}
