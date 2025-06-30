package org.example.honorsparkingbe.controller;

/**
 * CSRF 토큰 반환 컨트롤러 - 클라이언트가 CSRF 토큰을 요청하면 값을 반환
 */

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
public class CsrfController {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(
      CsrfController.class);

  @GetMapping("/csrf-token")
  public Map<String, String> getCsrfToken(HttpServletRequest request,
      HttpServletResponse response) {
    // ✅ 세션이 없으면 강제로 생성
    HttpSession session = request.getSession(true);
    log.debug("Session ID: {}", session.getId());

    // ✅ Spring Security가 저장해둔 CSRF 토큰 객체를 가져옴
    CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
    if (csrfToken == null) {
      // 스프링 CSRF 필터가 등록 안된 경우
      throw new IllegalStateException("CSRF protection is not enabled or token not generated yet");
    }
    log.debug("CSRF token value: {}", csrfToken.getToken());

    // ✅ 기존 쿠키 확인 (중복 발급 방지)
    String existingToken = null;
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("XSRF-TOKEN".equals(cookie.getName())) {
          existingToken = cookie.getValue();
          break;
        }
      }
    }

//    if (!csrfToken.getToken().equals(existingToken)) {
//      // ✅ 새로 발급할 필요가 있다면 쿠키로 넣기
//      ResponseCookie cookie = ResponseCookie.from("XSRF-TOKEN", csrfToken.getToken())
//          .path("/")
//          .httpOnly(false) // JS가 읽을 수 있어야 함
//          .secure(true)    // https 전용
//          .sameSite("None") // cross-site 요청을 허용할 경우
//          .build();
//      response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//
//      log.debug("Added XSRF-TOKEN cookie: {}", cookie.toString());
//    } else {
//      log.debug("Reusing existing XSRF-TOKEN cookie");
//    }

    // ✅ 프론트에서 헤더로 보낼 때 필요한 정보
    return Map.of(
        "headerName", csrfToken.getHeaderName(),  // usually X-CSRF-TOKEN
        "parameterName", csrfToken.getParameterName(),
        "token", csrfToken.getToken()
    );
  }
}

