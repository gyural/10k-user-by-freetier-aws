package org.example.honorsparkingbe.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.example.honorsparkingbe.controller.AlarmController;
import org.example.honorsparkingbe.service.AlarmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Disabled;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class) // Mockito 테스트 환경 확장
public class AlarmControllerTest {

    @InjectMocks
    private AlarmController alarmController; // @InjectMocks 사용

    @Mock
    private AlarmService alarmService; // @Mock으로 모킹

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(alarmController).build();
    }

    /**
     * 인증이 필요한 테스트들은 AlarmControllerAuthTest로 이전
     * --------------------------------------------------
     * 1. 알람 목록 조회 테스트 (GET /api/v1/alarmAll)
     * 4. 알람 전체 삭제 테스트 (DELETE /api/v1/alarm/all)
     * --------------------------------------------------
     */

    /**
     * 2. 알람 읽음 처리 테스트 (PUT /api/v1/alarm)
     */
    @Test
    @DisplayName("알람 읽기 성공")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUpdateAlarmsToRead_Success() throws Exception {
        Map<String, Object> mockResponse = Map.of("success", true);

        when(alarmService.updateAlarmsToRead(anyList())).thenReturn(mockResponse);

        mockMvc.perform(put("/api/v1/alarm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"alarmIDList\": [1, 2, 3]}")
                        .with(csrf()))
                .andExpect(status().isOk()) // 200 반환
                .andExpect(jsonPath("$.success").value(true)); // { "success": true } 인가?
    }

    @Test
    @DisplayName("이미 읽음 처리된 알람 요청 시 응답 확인")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUpdateAlarmsToRead_AlreadyReadAlarms_ShouldReturnCorrectResponse() throws Exception {
        // Given: 1번은 이미 읽음, 2번과 3번은 새롭게 읽음 처리됨
        Map<String, Object> mockResponse = Map.of(
                "success", true,
                "updatedIds", List.of(2L, 3L),    // 새롭게 읽음 처리된 알람 ID
                "alreadyReadIds", List.of(1L)     // 이미 읽음 상태였던 알람 ID
        );

        when(alarmService.updateAlarmsToRead(anyList())).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/alarm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"alarmIDList\": [1, 2, 3]}") // 1번은 이미 읽음, 2번과 3번은 새롭게 읽음
                        .with(csrf()))
                .andExpect(status().isOk())  // 200 응답
                .andExpect(jsonPath("$.success").value(true)) // 성공 여부 확인
                .andExpect(jsonPath("$.updatedIds").isArray()) // 배열 형태 확인
                .andExpect(jsonPath("$.updatedIds[0]").value(2L)) // 새롭게 읽음 처리된 ID 확인
                .andExpect(jsonPath("$.updatedIds[1]").value(3L))
                .andExpect(jsonPath("$.alreadyReadIds").isArray()) // 이미 읽음 처리된 ID 리스트 확인
                .andExpect(jsonPath("$.alreadyReadIds[0]").value(1L)); // 1번은 이미 읽음 상태였음
    }

    @Test
    @DisplayName("존재하지 않는 알람 ID 요청 시 400 반환")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUpdateAlarmsToRead_NonExistentId_ShouldReturnBadRequest() throws Exception {
        // Given: 존재하지 않는 ID 요청 시 예외 발생
        when(alarmService.updateAlarmsToRead(anyList()))
                .thenThrow(new IllegalArgumentException("No alarms found with given IDs"));

        // When & Then
        mockMvc.perform(put("/api/v1/alarm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"alarmIDList\": [9999]}") // 존재하지 않는 ID 요청
                        .with(csrf()))
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.error").value("No alarms found with given IDs")); // 에러 메시지 검증
    }

    /**
     * 3. 알람 선택 삭제 테스트 (DELETE /api/v1/alarm)
     */
    @Test
    @DisplayName("알람 삭제 성공")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testDeleteAlarms_Success() throws Exception {
        Map<String, Object> mockResponse = Map.of("success", true, "deletedIds", List.of(1L, 2L, 3L));

        when(alarmService.deleteAlarms(anyList())).thenReturn(mockResponse);

        mockMvc.perform(delete("/api/v1/alarm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"alarmIDList\": [1, 2, 3]}")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.deletedIds").isArray());
    }

    @Test
    @DisplayName("존재하지 않는 알람 삭제 요청 시 응답 확인")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testDeleteAlarms_NonExistingAlarms_ShouldReturnFailure() throws Exception {
        // Given: 삭제할 알람이 없는 경우
        Map<String, Object> mockResponse = Map.of("success", false, "deletedIds", List.of());

        when(alarmService.deleteAlarms(anyList())).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(delete("/api/v1/alarm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"alarmIDList\": [999, 1000, 1001]}") // 존재하지 않는 ID
                        .with(csrf()))
                .andExpect(status().isOk())  // 요청 자체는 정상 처리 (200)
                .andExpect(jsonPath("$.success").value(false)) // 실패 여부 확인 (success=false)
                .andExpect(jsonPath("$.deletedIds").isArray()) // deletedIds는 배열인지 확인
                .andExpect(jsonPath("$.deletedIds.length()").value(0)); // 삭제된 항목이 없어야 함
    }

//    /**
//     * 4. 알람 전체 삭제 테스트 (DELETE /api/v1/alarm/all)
//     */
//    @Test
//    @DisplayName("알람 전체 삭제 성공")
//    @WithMockUser(username = "testuser", roles = {"USER"})
//    void testDeleteAllAlarms_Success() throws Exception {
//        Map<String, Object> mockResponse = Map.of("success", true, "deletedCount", 10);
//
//        when(alarmService.deleteAllAlarms(any(Long.class))).thenReturn(mockResponse);
//
//        mockMvc.perform(delete("/api/v1/alarm/all").with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.deletedCount").value(10));
//    }

    /**
     * 5. 인증되지 않은 사용자의 요청 (401 Unauthorized)
     */
    @Disabled("비활성화된 테스트")
    @Test
    @DisplayName("인증되지 않은 사용자는 401 반환")
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/v1/alarmAll"))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized
    }
}
