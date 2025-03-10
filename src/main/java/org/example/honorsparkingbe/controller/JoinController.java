package org.example.honorsparkingbe.controller;

/**
 * 회원가입 요청 처리 컨트롤러
 */

import org.example.honorsparkingbe.dto.JoinDTO;
import org.example.honorsparkingbe.service.JoinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    /**
     * 회원가입
     * POST api/v1/join
     * @param joinDTO
     * @return
     */
    @PostMapping("/auth/join")
    public ResponseEntity<String> joinPost(@RequestBody JoinDTO joinDTO) {
        try {
            joinService.joinProcess(joinDTO);
            return ResponseEntity.status(201).body("Join process complete.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    /**
     * 아이디 중복 확인
     * GET api/v1/auth/check-authId?authId={}
     */
    @GetMapping("/auth/check-authId")
    public ResponseEntity<Boolean> checkAuthId(@RequestParam("authId") String authId) {
        Boolean isAvailable= joinService.isAuthIdAvailable(authId);
        return ResponseEntity.ok(isAvailable);
    }
}
