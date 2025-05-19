package org.example.honorsparkingbe.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncNonMemberInoutResponse {
    private String vehicleNumber;
    private String parkingLotLocation;
    private LocalDateTime entryTime;
    private Integer totalParkingMinutes;
    private Integer currentFee;
    private String entryPhotoUrl;
}
