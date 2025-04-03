package org.example.honorsparkingbe.controller;

import org.example.honorsparkingbe.dto.mypage.ChangeUserPasswordRequestDTO;
import org.example.honorsparkingbe.dto.mypage.GetUserInfoResponseDTO;
import org.example.honorsparkingbe.dto.mypage.UpdateUserCarNumberRequestDTO;
import org.example.honorsparkingbe.dto.mypage.UpdateUserInfoRequestDTO;
import org.example.honorsparkingbe.security.util.SecurityUtil;
import org.example.honorsparkingbe.service.MyPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/info")
    public ResponseEntity<GetUserInfoResponseDTO> getUserInfo() {
        GetUserInfoResponseDTO response= myPageService.getUserInfo();
        return ResponseEntity.ok(response);
    }

    /**
     * 현재 로그인된 사용자 정보 변경(전화번호, 이메일)
     * PUT /api/v1/mypage/username/info
     * @param request
     * @return
     */
    @PutMapping("/info")
    public ResponseEntity<Void> updateUserInfo(@RequestBody UpdateUserInfoRequestDTO request) {
        myPageService.updateUserInfo(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 차량 번호 변경
     * PUT /api/v1/mypage/username/car
     * @param request
     * @return
     */
    @PutMapping("/car")
    public ResponseEntity<Void> updateUserCarNumber(@RequestBody UpdateUserCarNumberRequestDTO request){
        myPageService.updateUserCarNumber(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호 변경
     * PUT /api/v1/mypage/username/password
     * @param request
     * @return
     */
    @PutMapping("/info/password")
    public ResponseEntity<Void> changeUserPassword(@RequestBody ChangeUserPasswordRequestDTO request){
        myPageService.changeUserPassword(request);
        return ResponseEntity.ok().build();
    }


}
