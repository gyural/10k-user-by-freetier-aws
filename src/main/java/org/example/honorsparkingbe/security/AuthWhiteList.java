package org.example.honorsparkingbe.security;

public class AuthWhiteList {

  public static final String[] PERMIT_ALL_PATHS = {
      "/api/v1/csrf-token",
      "/api/v1/",
      "/api/v1/auth/login/**",
      "/api/v1/auth/join",
      "/api/v1/phone-auth/send",
      "/api/v1/phone-auth/verify",
      "/confirm",
      "/swagger-ui/**",
      "/v3/api-docs/**",
      "api/v1/auth/check-authId",
      "api/v1/expo/**",
      "api/v1/auth/issue-cookie",
      "api/v1/auth/custom-session-login",
      "/api/v1/parking/nonmember",
      "/api/v1/",
      "/api/v1/auth/login/**",
      "/api/v1/auth/join",
      "/confirm",
      "/swagger-ui/**",
      "/v3/api-docs/**",
      "api/v1/auth/check-authId",
      "api/v1/sync/inout",
      "/.well-known/acme-challenge/"
  };
}
