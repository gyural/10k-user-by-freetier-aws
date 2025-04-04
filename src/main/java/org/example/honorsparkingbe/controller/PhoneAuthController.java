package org.example.honorsparkingbe.controller;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.request.PhoneAuthRequest;
import org.example.honorsparkingbe.dto.request.PhoneAuthVerifyRequest;
import org.example.honorsparkingbe.service.PhoneAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/phone-auth")
@RequiredArgsConstructor
public class PhoneAuthController {

    private final PhoneAuthService phoneAuthService;

    /**
     * SMS 전송
     * POST /api/v1/phone-auth/send
     * @param request
     * @return status 200, status 409, status 500
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendAuthCode(@RequestBody PhoneAuthRequest request) {

        try {
            phoneAuthService.sendAuthCode(request.getPhoneNumber());
            return ResponseEntity.ok("인증번호 전송 완료");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409 Conflict
        } catch (Exception e) {
            log.error("인증번호 전송 실패", e);  // 예외 내용과 스택트레이스 함께 로그에 남김
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("문자 전송 실패");
        }
    }

    /**
     * 인증번호 확인
     * POST /api/v1/phone-auth/verify
     * @param request
     * @return status 200, status 400
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyAuthCode(@RequestBody PhoneAuthVerifyRequest request) {

        boolean result = phoneAuthService.verifyAuthCode(
                request.getPhoneNumber(),
                request.getAuthCode()
        );

        return result
                ? ResponseEntity.ok("인증 성공")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증번호가 올바르지 않거나 만료되었습니다.");
    }
}