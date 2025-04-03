package org.example.honorsparkingbe.controller;

import org.example.honorsparkingbe.dto.mypage.GetUserInfoResponseDTO;
import org.example.honorsparkingbe.security.util.SecurityUtil;
import org.example.honorsparkingbe.service.MyPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    public MyPageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    /**
     * 현재 로그인된 사용자의 이름(실제 이름)을 가져온다.
     * GET /api/v1/mypage/username
     * @return
     */
    @GetMapping("/username")
    public ResponseEntity<Map<String, String>> getUsername() {
        String username= SecurityUtil.getCurrentUsername(); // static이라 의존성 주입 x, 클래스 직접 호출

        Map<String, String> response = new HashMap<>();
        response.put("username", username);

        return ResponseEntity.ok(response);
    }

    /**
     * 현재 로그인된 사용자의 정보 출력
     * GET /api/v1/mypage/username/info
     * @return
     */
    @GetMapping("/username/info")
    public ResponseEntity<GetUserInfoResponseDTO> getUserInfo() {
        GetUserInfoResponseDTO response= myPageService.getUserInfo();
        return ResponseEntity.ok(response);
    }



}
