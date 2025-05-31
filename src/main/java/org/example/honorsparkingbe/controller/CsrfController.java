package org.example.honorsparkingbe.controller;

/**
 * CSRF 토큰 반환 컨트롤러 - 클라이언트가 CSRF 토큰을 요청하면 값을 반환
 */

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
public class CsrfController {

  @GetMapping("/csrf-token")
  public Map<String, String> getCsrfToken(HttpServletRequest request,
      HttpServletResponse response) {
    // 세션을 강제로 생성 (없을 경우)
    request.getSession();

    // Spring Security가 제공하는 CsrfToken
    CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");

    // 이미 쿠키로 세팅되어 있다면 중복 설정하지 않음
    boolean tokenCookieExists = false;
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("XSRF-TOKEN".equals(cookie.getName())) {
          tokenCookieExists = true;
          break;
        }
      }
    }

    // XSRF-TOKEN 쿠키가 없다면 생성
    if (!tokenCookieExists) {
      ResponseCookie cookie = ResponseCookie.from("XSRF-TOKEN", csrfToken.getToken())
          .path("/")
          .httpOnly(false)
          .secure(true)
          .sameSite("None")
          .build();

      response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // 프론트에서 헤더로 보낼 때 필요한 정보 반환
    return Map.of(
        "headerName", csrfToken.getHeaderName(), // 일반적으로 X-CSRF-TOKEN
        "parameterName", csrfToken.getParameterName(),
        "token", csrfToken.getToken()
    );
  }
}

