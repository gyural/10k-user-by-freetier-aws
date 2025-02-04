package org.example.honorsparkingbe.dto.response;

import lombok.*;
import org.example.honorsparkingbe.dto.ParkingZoneDTO;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ParkingZoneListResponse {
    private List<ParkingZoneDTO> parkingZones;
    private PaginationInfo pagination;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaginationInfo {
        private Long currentPage;
        private Long totalPages;
        private Long pagePerItem;
        private Long totalItems;
    }
}
