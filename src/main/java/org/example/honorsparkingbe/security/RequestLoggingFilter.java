//package org.example.honorsparkingbe.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Enumeration;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//@Component
//public class RequestLoggingFilter extends OncePerRequestFilter {
//
//  @Override
//  protected void doFilterInternal(HttpServletRequest request,
//      HttpServletResponse response,
//      FilterChain filterChain) throws ServletException, IOException {
//
//    System.out.println("=================================");
//    HttpServletRequest httpRequest = (HttpServletRequest) request;
//    CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(httpRequest);
//
//    System.out.println("====== 🚨 Incoming Request ======");
//    System.out.println("🔗 Method: " + wrappedRequest.getMethod());
//    System.out.println("🔗 URI: " + wrappedRequest.getRequestURI());
//    System.out.println("🔗 Query String: " + wrappedRequest.getQueryString());
//    System.out.println("🧭 Protocol: " + wrappedRequest.getProtocol());
//
//    System.out.println("📦 Headers:");
//    Enumeration<String> headerNames = wrappedRequest.getHeaderNames();
//    while (headerNames.hasMoreElements()) {
//      String headerName = headerNames.nextElement();
//      String headerValue = wrappedRequest.getHeader(headerName);
//      System.out.println("  - " + headerName + ": " + headerValue);
//    }
//
//    System.out.println("🍪 Cookies:");
//    Cookie[] cookies = wrappedRequest.getCookies();
//    if (cookies != null) {
//      for (Cookie cookie : cookies) {
//        System.out.println("  - " + cookie.getName() + "=" + cookie.getValue());
//      }
//    } else {
//      System.out.println("  (No Cookies)");
//    }
//
////    System.out.println("🧭 Parameters:");
////    wrappedRequest.getParameterMap()
////        .forEach((k, v) -> System.out.println("  - " + k + ": " + String.join(",", v)));
//
////    System.out.println("📜 Body:");
////    String body = wrappedRequest.getReader().lines()
////        .collect(Collectors.joining(System.lineSeparator()));
////    System.out.println(body);
////
////    System.out.println("=================================");
//
//    filterChain.doFilter(wrappedRequest, response);
//  }
//}