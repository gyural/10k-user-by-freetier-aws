package org.example.honorsparkingbe.unit.service;

import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.example.honorsparkingbe.service.PhoneAuthService;
import org.example.honorsparkingbe.util.CoolSmsUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class PhoneAuthServiceTest {

    private PhoneAuthService phoneAuthService;
    private StringRedisTemplate redisTemplate;
    private MemberRepository memberRepository;
    private CoolSmsUtil coolSmsUtil;
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        memberRepository = mock(MemberRepository.class);
        coolSmsUtil = mock(CoolSmsUtil.class);
        valueOperations = (ValueOperations<String, String>) mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        phoneAuthService = new PhoneAuthService(redisTemplate, coolSmsUtil, memberRepository);
    }

    /**
     * 1. 정상적인 인증번호 전송 테스트
     */
    @Test
    void testSendAuthCodeSuccess() {
        // given
        String phoneNumber = "01012345678";
        when(memberRepository.existsByPhoneNumber(phoneNumber)).thenReturn(false);

        // when
        phoneAuthService.sendAuthCode(phoneNumber);

        // then
        verify(valueOperations).set(
                eq("phoneauth:" + phoneNumber),
                anyString(),
                eq(Duration.ofMinutes(3))
        );
        verify(coolSmsUtil).sendSms(any(), any(), any(), eq(phoneNumber), contains("인증번호"));
    }

    /**
     * 2. 이미 가입된 전화번호일 경우 예외 테스트
     */
    @Test
    void testSendAuthCode_DuplicatePhoneNumber_ThrowsException() {
        // given
        String phoneNumber = "01012345678";
        when(memberRepository.existsByPhoneNumber(phoneNumber)).thenReturn(true);

        // expect
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> phoneAuthService.sendAuthCode(phoneNumber)
        );

        assertEquals("이미 가입된 전화번호입니다.", exception.getMessage());
        verify(coolSmsUtil, never()).sendSms(any(), any(), any(), any(), any());
    }

    /**
     * 3. 인증번호가 일치할 경우 인증 성공 테스트
     */
    @Test
    void testVerifyAuthCode_Success() {
        // given
        String phoneNumber = "01012345678";
        String inputCode = "123456";
        when(valueOperations.get("phoneauth:" + phoneNumber)).thenReturn(inputCode);

        // when
        boolean result = phoneAuthService.verifyAuthCode(phoneNumber, inputCode);

        // then
        assertTrue(result);
        verify(redisTemplate).delete("phoneauth:" + phoneNumber);
    }

    /**
     * 4. 인증번호가 틀릴 경우 인증 실패 테스트
     */
    @Test
    void testVerifyAuthCode_Failure() {
        // given
        String phoneNumber = "01012345678";
        String inputCode = "123456";
        when(valueOperations.get("phoneauth:" + phoneNumber)).thenReturn("654321");

        // when
        boolean result = phoneAuthService.verifyAuthCode(phoneNumber, inputCode);

        // then
        assertFalse(result);
        verify(redisTemplate, never()).delete(anyString());

    }
}