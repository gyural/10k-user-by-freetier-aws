package org.example.honorsparkingbe.security;
/**
 * OAuth2 성공 핸들러 (리다이렉트만 수행)
 */

import org.example.honorsparkingbe.dto.CustomOAuth2User;
import org.example.honorsparkingbe.dto.OAuth2Response;
import org.example.honorsparkingbe.util.AesUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CustomOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AesUtil aesUtil;

    public CustomOAuth2LoginSuccessHandler(AesUtil aesUtil) {
        this.aesUtil = aesUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 로그인 성공 후 프론트엔드로 리디렉트
        String sessionId = request.getSession().getId();
        String encryptedSessionId = aesUtil.encrypt(sessionId);
        System.out.println("✅ OAuth2 로그인 성공 - 세션 ID: " + sessionId);

        // response.sendRedirect("http://localhost:3000/oauth2/success?sessionId=" + encryptedSessionId);
        response.sendRedirect("https://honorsparking-web.vercel.app/sessionlogin?sessionId=" + encryptedSessionId);
    }
}