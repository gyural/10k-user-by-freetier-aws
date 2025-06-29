package org.example.honorsparkingbe.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {

    // requst 출력
    System.out.println("========= 🚨 Access Denied Handler Called =========");

    // 메서드 & URI
    System.out.println("🔗 Method: " + request.getMethod());
    System.out.println("🔗 Request URI: " + request.getRequestURI());
    System.out.println("🔗 Query String: " + request.getQueryString());
    // 요청 헤더
    // 헤더
    System.out.println("📦 Headers:");
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      String headerValue = request.getHeader(headerName);
      System.out.println("  - " + headerName + ": " + headerValue);
    }

    // 파라미터
    System.out.println("🧭 Parameters:");
    request.getParameterMap().forEach((key, values) -> {
      System.out.println("  - " + key + ": " + Arrays.toString(values));
    });

    // 쿠키
    System.out.println("🍪 Cookies:");
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        System.out.println("  - " + cookie.getName() + "=" + cookie.getValue());
      }
    } else {
      System.out.println("  (No Cookies)");
    }

    System.out.println("❌❌ Access Denied!!");

    // 예외 메시지
    System.out.println("🛑 Exception Message: " + accessDeniedException.getMessage());
    accessDeniedException.printStackTrace();
    
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.getWriter().write("{\"error\":\"Forbidden - You don't have permission\"}");
  }
}
