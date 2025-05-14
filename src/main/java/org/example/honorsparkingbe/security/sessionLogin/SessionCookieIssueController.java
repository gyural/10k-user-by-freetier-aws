//package org.example.honorsparkingbe.security.sessionLogin;
//
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseCookie;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/auth")
//public class SessionCookieIssueController {
//
//    @PostMapping("/issue-cookie")
//    public ResponseEntity<?> issueSessionCookie(@RequestBody Map<String, String> request,
//                                                HttpServletResponse response) {
//        String base64SessionId = request.get("encodedSessionId");
//
//        if (base64SessionId == null || base64SessionId.isBlank()) {
//            return ResponseEntity.badRequest().body("encodedSessionId 누락");
//        }
//
//        // 1. Base64 → 실제 세션 ID 복호화
//        String sessionId;
//        try {
//            byte[] decodedBytes = Base64.getDecoder().decode(base64SessionId);
//            sessionId = new String(decodedBytes, StandardCharsets.UTF_8);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body("유효하지 않은 Base64 문자열");
//        }
//
//        // 2. Set-Cookie: SESSION=... 쿠키 생성
//        ResponseCookie sessionCookie = ResponseCookie.from("SESSION", sessionId)
//                .httpOnly(true)
//                .secure(false) // 개발 환경일 경우 false / 배포 시 true + HTTPS
//                .sameSite("Lax") // 또는 "None" (WebView나 크로스 도메인일 경우)
//                .path("/")
//                .build();
//
//        response.addHeader(HttpHeaders.SET_COOKIE, sessionCookie.toString());
//
//        return ResponseEntity.ok("SESSION 쿠키 발급 완료");
//    }
//}
//
