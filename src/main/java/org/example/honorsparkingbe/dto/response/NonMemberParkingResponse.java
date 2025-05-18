package org.example.honorsparkingbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NonMemberParkingResponse {
    private String vehicleNumber;     // 차량 번호
    private String parkingZoneName;   // 주차장 이름 (주차장 위치)
    private LocalDateTime entryTime;  // 입차 시간
    private Integer currentFee;       // 현재 요금
}

//4