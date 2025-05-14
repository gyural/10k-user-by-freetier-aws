package org.example.honorsparkingbe.security.sessionLogin;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

@RestController
public class CustomSessionLoginController {

    @Autowired
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @PostMapping("/api/v1/auth/custom-session-login")
    public ResponseEntity<?> loginWithSessionId(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String sessionId = body.get("sessionId");
        if (sessionId == null) return ResponseEntity.badRequest().build();

        // Redis에서 세션 가져오기
        System.out.println("여기까지 ok"+ sessionId);
        try {
            Session session = sessionRepository.findById(sessionId);
            System.out.println("session = " + session);
        } catch (Exception e) {
            e.printStackTrace(); // 어떤 놈이 역직렬화 실패했는지
        }

        Session springSession = sessionRepository.findById(sessionId); // 아니 이거 왜 안되는데
        System.out.println("Redis 세션 조회 결과: " + springSession);
        if (springSession == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session");
        }

        // SecurityContext 복원
        SecurityContext context = springSession.getAttribute("SPRING_SECURITY_CONTEXT");
        if (context == null || context.getAuthentication() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authentication info");
        }

        // 기존 세션 만료 (Redis에서 삭제) $$$$$$$
        sessionRepository.deleteById(sessionId); // ❗ 기존 세션 무효화

        // 현재 SecurityContextHolder에 적용
        SecurityContextHolder.setContext(context);

        // 이후 요청에서도 인증 상태가 유지되도록 현재 세션에 저장
        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );

        return ResponseEntity.ok("Logged in with session ID");
    }
}
