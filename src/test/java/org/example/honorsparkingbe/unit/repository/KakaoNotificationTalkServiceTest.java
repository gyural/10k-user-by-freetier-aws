package org.example.honorsparkingbe.unit.repository;

import org.example.honorsparkingbe.service.KakaoNotificationTalkService;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

public class KakaoNotificationTalkServiceTest {

  @InjectMocks
  KakaoNotificationTalkService kakaoNotificationTalkService;

  @Spy
  KakaoNotificationTalkService realService =
      new KakaoNotificationTalkService(
          "testServiceId", "testAccessKey", "testSecretKey",
          "@plusFriendId", "entryTemplate", "exitTemplate"
      );

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testSendCarEntryAlarm_ValidInput_ShouldCallSendAlimTalk() {
    // given
    String phoneNumber = "01012345678";
    String carNumber = "12가3456";
    LocalDateTime now = LocalDateTime.of(2025, 4, 11, 14, 30, 0);

    // when
    doNothing().when(realService).sendAlimTalk(anyString(), anyString(), anyString(), any(JSONArray.class), anyBoolean(), any());

    realService.sendcarEntryAlarm(phoneNumber, carNumber, now);

    // then
    verify(realService, times(1)).sendAlimTalk(
        eq(phoneNumber),
        eq("entryTemplate"),
        contains(carNumber),
        any(JSONArray.class),
        eq(false),
        isNull()
    );
  }

  @Test
  void testSendCarExitAlarm_ValidInput_ShouldCallSendAlimTalk() {
    // given
    String phoneNumber = "01087654321";
    LocalDateTime now = LocalDateTime.now();

    // when
    doNothing().when(realService).sendAlimTalk(anyString(), anyString(), anyString(), any(JSONArray.class), anyBoolean(), any());

    realService.sendCarExitAlarm(phoneNumber, now);

    // then
    verify(realService, times(1)).sendAlimTalk(
        eq(phoneNumber),
        eq("exitTemplate"),
        contains("출차되었습니다"),
        any(JSONArray.class),
        eq(false),
        isNull()
    );
  }

  @Test
  void testSendCarEntryAlarm_NullEntranceTime_ShouldNotCallSendAlimTalk() {
    // when
    realService.sendcarEntryAlarm("01012345678", "12가3456", null);

    // then
    verify(realService, times(0)).sendAlimTalk(any(), any(), any(), any(), anyBoolean(), any());
  }

}

