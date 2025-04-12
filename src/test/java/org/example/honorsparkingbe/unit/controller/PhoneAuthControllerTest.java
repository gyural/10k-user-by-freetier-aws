package org.example.honorsparkingbe.unit.controller;

import org.example.honorsparkingbe.controller.PhoneAuthController;
import org.example.honorsparkingbe.dto.request.PhoneAuthRequest;
import org.example.honorsparkingbe.dto.request.PhoneAuthVerifyRequest;
import org.example.honorsparkingbe.service.PhoneAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PhoneAuthControllerTest {

    @InjectMocks
    private PhoneAuthController phoneAuthController;

    @Mock
    private PhoneAuthService phoneAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("인증번호 전송 성공")
    void testSendAuthCodeSuccess() {
        // given
        PhoneAuthRequest request = new PhoneAuthRequest();
        request.setPhoneNumber("01012345678");

        // when
        ResponseEntity<String> response = phoneAuthController.sendAuthCode(request);

        // then
        verify(phoneAuthService).sendAuthCode("01012345678");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("인증번호 전송 완료", response.getBody());
    }

    @Test
    @DisplayName("이미 가입된 번호로 인증번호 전송 시 409 반환")
    void testSendAuthCode_DuplicatePhoneNumber() {
        // given
        PhoneAuthRequest request = new PhoneAuthRequest();
        request.setPhoneNumber("01012345678");

        doThrow(new IllegalStateException("이미 가입된 전화번호입니다."))
                .when(phoneAuthService).sendAuthCode("01012345678");

        // when
        ResponseEntity<String> response = phoneAuthController.sendAuthCode(request);

        // then
        assertEquals(409, response.getStatusCodeValue());
        assertEquals("이미 가입된 전화번호입니다.", response.getBody());
    }

    @Test
    @DisplayName("예상치 못한 오류 발생 시 500 반환")
    void testSendAuthCode_InternalServerError() {
        // given
        PhoneAuthRequest request = new PhoneAuthRequest();
        request.setPhoneNumber("01012345678");

        doThrow(new RuntimeException("서버 오류"))
                .when(phoneAuthService).sendAuthCode("01012345678");

        // when
        ResponseEntity<String> response = phoneAuthController.sendAuthCode(request);

        // then
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("문자 전송 실패", response.getBody());
    }

    @Test
    @DisplayName("인증번호 맞을 시 200 반환")
    void testVerifyAuthCodeSuccess() {
        // given
        PhoneAuthVerifyRequest request = new PhoneAuthVerifyRequest();
        request.setPhoneNumber("01012345678");
        request.setAuthCode("123456");

        when(phoneAuthService.verifyAuthCode("01012345678", "123456")).thenReturn(true);

        // when
        ResponseEntity<String> response = phoneAuthController.verifyAuthCode(request);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("인증 성공", response.getBody());
    }

    @Test
    @DisplayName("인증번호 틀릴 경우 400 반환")
    void testVerifyAuthCodeFailure() {
        // given
        PhoneAuthVerifyRequest request = new PhoneAuthVerifyRequest();
        request.setPhoneNumber("01012345678");
        request.setAuthCode("wrong");

        when(phoneAuthService.verifyAuthCode("01012345678", "wrong")).thenReturn(false);

        // when
        ResponseEntity<String> response = phoneAuthController.verifyAuthCode(request);

        // then
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("인증번호가 올바르지 않거나 만료되었습니다.", response.getBody());
    }
}
