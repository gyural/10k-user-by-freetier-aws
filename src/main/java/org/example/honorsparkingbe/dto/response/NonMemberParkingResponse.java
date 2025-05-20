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
    private String vehicleNumber;
    private String parkingLotLocation;
    private LocalDateTime entryTime;
    private Integer totalParkingMinutes;
    private Integer currentFee;
    private String entryPhotoUrl;
}

//4