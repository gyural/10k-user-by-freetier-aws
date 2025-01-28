package org.example.honorsparkingbe.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OAuthControllerTest {

    private MockMvc mockMvc;

    @Test
    public void testRedirectToGoogle() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new OAuthRedirectController()).build();

        mockMvc.perform(get("/api/v1/auth/login/oauth/google"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/oauth2/authorization/google"));
    }

    @Test
    public void testRedirectToKakao() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new OAuthRedirectController()).build();

        mockMvc.perform(get("/api/v1/auth/login/oauth/kakao"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/oauth2/authorization/kakao"));
    }

    @Test
    public void testRedirectToNaver() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new OAuthRedirectController()).build();

        mockMvc.perform(get("/api/v1/auth/login/oauth/naver"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/oauth2/authorization/naver"));
    }
}
