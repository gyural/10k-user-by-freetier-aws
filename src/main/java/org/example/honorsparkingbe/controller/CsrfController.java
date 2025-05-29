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

  static String CSRF_KEY = "XSRF-TOKEN";

  @GetMapping("/csrf-token")
  public Map<String, String> getCsrfToken(HttpServletRequest request,
      HttpServletResponse response) {
    request.getSession();

    CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");

    Cookie[] cookies = request.getCookies();
    boolean alreadyExists = false;
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (CSRF_KEY.equals(cookie.getName())) {
          alreadyExists = true;
          break;
        }
      }
    }

    if (!alreadyExists) {
      ResponseCookie cookie = ResponseCookie.from(CSRF_KEY, csrfToken.getToken())
          .path("/")
          .httpOnly(false)
          .secure(true)
          .sameSite("None")
          .build();

      response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    return Map.of(
        "headerName", csrfToken.getHeaderName(),
        "parameterName", csrfToken.getParameterName(),
        "token", csrfToken.getToken()
    );
  }
}

