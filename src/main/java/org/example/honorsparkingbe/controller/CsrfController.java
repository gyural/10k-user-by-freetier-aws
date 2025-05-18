package org.example.honorsparkingbe.controller;

/**
 * CSRF 토큰 반환 컨트롤러
 * - 클라이언트가 CSRF 토큰을 요청하면 값을 반환
 */

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api/v1")
public class CsrfController {
    @GetMapping("/csrf-token")
    public Map<String, String> getCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        request.getSession();

        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        // return csrfToken.getToken(); // CSRF 토큰 값을 반환

        // Set-Cookie 수동 설정 (SameSite=None; Secure)
        ResponseCookie cookie = ResponseCookie.from("XSRF-TOKEN", csrfToken.getToken())
                .path("/")
                .httpOnly(false)
                .secure(true)
                .sameSite("None")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());


        return Map.of(
                "headerName", csrfToken.getHeaderName(),
                "parameterName", csrfToken.getParameterName(),
                "token", csrfToken.getToken()
        );
    }
}

