package org.example.honorsparkingbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.honorsparkingbe.dto.JoinDTO;
import org.example.honorsparkingbe.service.JoinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class JoinControllerTest {

    private MockMvc mockMvc;
    private JoinService joinService;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        joinService = mock(JoinService.class);
        JoinController joinController = new JoinController(joinService);
        mockMvc = MockMvcBuilders.standaloneSetup(joinController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testJoinSuccess() throws Exception {
        JoinDTO joinDTO = new JoinDTO();
        joinDTO.setAuthId("testuser");
        joinDTO.setPassword("testpassword");
        joinDTO.setUserName("Test User");
        joinDTO.setPhoneNumber("010-1234-5678");
        joinDTO.setEmail("test@example.com");
        joinDTO.setBirthdayYear(1990);
        joinDTO.setBirthday("0101");

        doNothing().when(joinService).joinProcess(Mockito.any(JoinDTO.class));

        mockMvc.perform(post("/api/v1/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinDTO)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Join process complete."));
    }

    @Test
    public void testJoinFailure_DuplicateAuthId() throws Exception {
        JoinDTO joinDTO = new JoinDTO();
        joinDTO.setAuthId("testuser");

        doThrow(new IllegalArgumentException("authId= testuser already exists."))
                .when(joinService).joinProcess(Mockito.any(JoinDTO.class));

        mockMvc.perform(post("/api/v1/auth/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("authId= testuser already exists."));
    }
}
