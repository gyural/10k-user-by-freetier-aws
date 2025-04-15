package org.example.honorsparkingbe.controller;

import org.example.honorsparkingbe.dto.expo.ExpoRequestDTO;
import org.example.honorsparkingbe.service.ExpoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/expo")
public class ExpoController {

    private final ExpoService expoService;

    public ExpoController(ExpoService expoService) {
        this.expoService = expoService;
    }

    /**
     * 프론트로부터 토큰과 유저Id 받기
     * POST /api/v1/expo
     * @param request
     * @return
     */
    @PostMapping("/push-token")
    public ResponseEntity<String> pushToken(@RequestBody ExpoRequestDTO request) {
        expoService.pushToken(request);
        return ResponseEntity.ok("request saved");
    }
}
