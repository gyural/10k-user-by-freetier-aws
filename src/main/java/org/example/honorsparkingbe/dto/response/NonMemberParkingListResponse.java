package org.example.honorsparkingbe.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NonMemberParkingListResponse {
    private List<NonMemberParkingResponse> parkingEntries;
}