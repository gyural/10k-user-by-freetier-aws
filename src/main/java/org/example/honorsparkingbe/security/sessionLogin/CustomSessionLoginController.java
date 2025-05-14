//package org.example.honorsparkingbe.security.sessionLogin;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.session.Session;
//import org.springframework.session.FindByIndexNameSessionRepository;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.session.FindByIndexNameSessionRepository;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.session.data.redis.RedisIndexedSessionRepository;
//
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/auth")
//public class CustomSessionLoginController {
//
//    private final RedisIndexedSessionRepository sessionRepository;
//
//    public CustomSessionLoginController(RedisIndexedSessionRepository sessionRepository) {
//        this.sessionRepository = sessionRepository;
//    }
//
//    @PostMapping("/custom-session-login")
//    public ResponseEntity<?> loginWithSessionId(@RequestBody Map<String, String> request,
//                                                HttpServletRequest httpRequest,
//                                                HttpServletResponse httpResponse) {
//
//        String sessionId = request.get("sessionId"); // 나중에 암호화 해제 예정
//        if (sessionId == null) {
//            return ResponseEntity.badRequest().body("sessionId 누락");
//        }
//
//        Session session = sessionRepository.findById(sessionId);
//        if (session == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 세션");
//        }
//
//        Object context = session.getAttribute("SPRING_SECURITY_CONTEXT");
//        if (!(context instanceof SecurityContext securityContext)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("보안 컨텍스트 없음");
//        }
//
////        Authentication originalAuth = securityContext.getAuthentication();
////        SecurityContextHolder.getContext().setAuthentication(originalAuth);
////
////        // 현재 요청에서 HttpSession을 강제로 생성 → Set-Cookie 자동 설정됨
////        httpRequest.getSession(true);
//
//        Authentication originalAuth = securityContext.getAuthentication();
//
//        // Spring Security 컨텍스트에 인증 객체 설정
//        SecurityContextHolder.getContext().setAuthentication(originalAuth);
//
//        // ✅ 기존 세션을 새로 만들고, SPRING_SECURITY_CONTEXT를 다시 설정해야 Set-Cookie 응답이 발생함
//        HttpSession httpSession = httpRequest.getSession(true);
//        httpSession.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
//
//
//        return ResponseEntity.ok("세션 로그인 성공");
//    }
//}
//
