package org.example.honorsparkingbe.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SyncInoutResponse {

  List<ParkingEntry> ValidNonExitEntries;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ParkingEntry {

    private Long id;

    // 비회원 차량 조회를 위해 필요한 필드
    private String vehicleNumber;
    private String parkingZoneName;
    private LocalDateTime entryTime;
    private Integer currentFee;

  }
}
