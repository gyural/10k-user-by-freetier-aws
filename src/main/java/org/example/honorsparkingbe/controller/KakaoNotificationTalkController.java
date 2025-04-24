package org.example.honorsparkingbe.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.service.KakaoNotificationTalkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alimtalk")
@RequiredArgsConstructor
public class KakaoNotificationTalkController {

  private final KakaoNotificationTalkService kakaoNotificationTalkService;

  @PostMapping("/entry")
  public ResponseEntity<String> sendEntryAlarm(@RequestBody PhoneOnlyRequest request) {
    LocalDateTime currentTime = LocalDateTime.now();

    kakaoNotificationTalkService.sendcarEntryAlarm(
        request.getUserPhoneNumber(),
        "12가 3456", // 차량번호는 더미값
        currentTime
    );

    return ResponseEntity.ok("입차 알림톡 전송 완료");
  }

  @Data
  public static class PhoneOnlyRequest {
    private String userPhoneNumber;
    private String dummy = "더미"; // 형식 맞추기용
  }

  @PostMapping("/exit")
  public ResponseEntity<String> sendCarExitAlarm(@RequestBody PhoneOnlyRequest request) {
    // 더미 값 세팅
    LocalDateTime entranceTime = LocalDateTime.now(); // 현재 시간 사용

    kakaoNotificationTalkService.sendCarExitAlarm(
        request.getUserPhoneNumber(),
        entranceTime
    );

    return ResponseEntity.ok("출차 알림톡 전송 완료");
  }

  @Data
  public static class OnlyPhoneRequest {
    private String userPhoneNumber;
  }

}
