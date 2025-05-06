package org.example.honorsparkingbe.controller;

/**
 * 로그아웃 요청 처리 컨트롤러
 */

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.honorsparkingbe.security.util.SecurityUtil;
import org.example.honorsparkingbe.service.ExpoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class LogoutController {

    private final ExpoService expoService;

    public LogoutController(ExpoService expoService) {
        this.expoService = expoService;
    }

    @GetMapping("/logout")
    public ResponseEntity<String>  logout(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 로그아웃 전, expo 토큰 제거 수행
        Long id= SecurityUtil.getCurrentUserId();
        System.out.println("현재 로그인된 사용자의 Id = " + id);
        expoService.deleteToken(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return ResponseEntity.ok("logout succcessful");
    }
}
