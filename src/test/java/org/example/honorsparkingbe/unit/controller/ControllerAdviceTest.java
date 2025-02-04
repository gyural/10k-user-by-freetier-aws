package org.example.honorsparkingbe.unit.controller;

import org.apache.catalina.security.SecurityConfig;
import org.example.honorsparkingbe.Slf4jRestControllerAdvice;
import org.example.honorsparkingbe.mock.WithCustomMockUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;


import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import({Slf4jRestControllerAdvice.class, ControllerAdviceTest.TestController.class})
@ContextConfiguration(classes = SecurityConfig.class)
@AutoConfigureMockMvc
@WebAppConfiguration
@ExtendWith(SpringExtension.class)

public class ControllerAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;



    // 샘플 컨트롤러
    @RestController
    @RequestMapping("/test")
    public static class TestController {
        @GetMapping("/exception")
        public void throwException() {
            throw new RuntimeException("Test Exception");
        }

        @GetMapping("/illegal-argument")
        public void throwIllegalArgumentException() {
            throw new IllegalArgumentException("Illegal Argument");
        }

        @PostMapping("/validation")
        public void validateRequest( TestRequest request) {
            // 요청 바디 검증
            throw new IllegalArgumentException("Name must not be blank");
        }
    }

    // 요청 DTO (Validation 테스트용)
    public static class TestRequest {
        //@NotBlank(message = "Name must not be blank")
        private String name;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @BeforeEach
    void setupSecurityContext() {
        Authentication mockAuthentication = Mockito.mock(Authentication.class);
        SecurityContext mockSecurityContext = Mockito.mock(SecurityContext.class);

        when(mockAuthentication.getName()).thenReturn("test@example.com");
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);

        SecurityContextHolder.setContext(mockSecurityContext);

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Exception 처리 테스트")
    @WithCustomMockUser(email = "admin@example.com")
    void testHandleException() throws Exception {
        mockMvc.perform(get("/test/exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Test Exception"));
    }

    @Test
    @DisplayName("IllegalArgumentException 처리 테스트")
    @WithCustomMockUser(email = "admin@example.com")
    void testHandleIllegalArgumentException() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Illegal Argument"));
    }

    @Test
    @DisplayName("ValidationException 처리 테스트")
    @WithCustomMockUser(email = "admin@example.com")
    void testHandleValidationException() throws Exception {

        mvc.perform(post("/test/validation").with(csrf())
                        .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Name must not be blank")));
    }

}