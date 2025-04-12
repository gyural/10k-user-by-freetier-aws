package org.example.honorsparkingbe.unit.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.example.honorsparkingbe.controller.KakaoNotificationTalkController;
import org.example.honorsparkingbe.service.KakaoNotificationTalkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class KakaoNotificationTalkControllerTest {

  private KakaoNotificationTalkService kakaoNotificationTalkService;
  private KakaoNotificationTalkController controller;

  @BeforeEach
  void setUp() {
    kakaoNotificationTalkService = mock(KakaoNotificationTalkService.class);
    controller = new KakaoNotificationTalkController(kakaoNotificationTalkService);
  }

  @Test
  void testSendEntryAlarm() {
    KakaoNotificationTalkController.PhoneOnlyRequest request = new KakaoNotificationTalkController.PhoneOnlyRequest();
    request.setUserPhoneNumber("01012345678");

    ResponseEntity<String> response = controller.sendEntryAlarm(request);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("입차 알림톡 전송 완료", response.getBody());

    verify(kakaoNotificationTalkService).sendcarEntryAlarm(
        eq("01012345678"),
        eq("12가 3456"),
        any(LocalDateTime.class)
    );
  }

  @Test
  void testSendExitAlarm() {
    KakaoNotificationTalkController.PhoneOnlyRequest request = new KakaoNotificationTalkController.PhoneOnlyRequest();
    request.setUserPhoneNumber("01098765432");

    ResponseEntity<String> response = controller.sendCarExitAlarm(request);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("출차 알림톡 전송 완료", response.getBody());

    verify(kakaoNotificationTalkService).sendCarExitAlarm(
        eq("01098765432"),
        any(LocalDateTime.class)
    );
  }
}
