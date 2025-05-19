package org.example.honorsparkingbe.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncNonMemberInoutListResponse {
    private List<SyncNonMemberInoutResponse> parkingEntries;
}