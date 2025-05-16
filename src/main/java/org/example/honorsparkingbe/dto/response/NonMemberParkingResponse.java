package org.example.honorsparkingbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NonMemberParkingResponse {
    private boolean isParked;
    private String message;
}
//4