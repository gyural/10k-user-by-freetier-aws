package org.example.honorsparkingbe.unit.controller;

import org.example.honorsparkingbe.mock.WithCustomMockUser;
import org.example.honorsparkingbe.service.AlarmService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AlarmController.class)
class AlarmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlarmService alarmService;

    @Test
    @WithCustomMockUser(email = "user@example.com")  // 🔥 Mock 로그인 사용자 추가
    void testGetAlarms_Success() throws Exception {
        // Given
        Map<String, Object> mockResponse = Map.of(
                "alarms", List.of(),
                "pagination", Map.of("currentPage", 1, "totalPages", 1, "pageSize", 10, "totalItems", 0)
        );
        Mockito.when(alarmService.getAlarms(1L, null, 0, 10)).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/alarmAll")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alarms").isArray())
                .andExpect(jsonPath("$.pagination.currentPage").value(1));
    }
}
