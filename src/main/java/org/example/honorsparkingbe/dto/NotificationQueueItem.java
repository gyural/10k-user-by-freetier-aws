package org.example.honorsparkingbe.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.honorsparkingbe.domain.enums.NotiChannel;
import org.example.honorsparkingbe.domain.enums.NotiEventType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificationQueueItem {

  NotiChannel notiChannel;
  NotiEventType notiEventType;

  String carNumber;
  LocalDateTime entranceTime;

  String phoneNumber; // only kakao
  String notiTitle; // only push
  String notiBody; // only push
  String pushToken; // only push

  Integer retryCount;
}