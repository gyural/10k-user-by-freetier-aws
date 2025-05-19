package org.example.honorsparkingbe.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncNonMemberInoutRequest {
    private String vehicleNumber;
}
