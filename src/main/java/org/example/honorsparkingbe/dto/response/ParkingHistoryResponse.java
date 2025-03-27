package org.example.honorsparkingbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ParkingHistoryResponse {
    private List<ParkingHistoryItem> parkingHistories;
    private PaginationResponse pagination;

    @Getter
    @AllArgsConstructor
    public static class ParkingHistoryItem {
        private Long id;
        private String zoneName;
        private LocalDateTime entranceTime;
        private LocalDateTime exitTime;
        private int amount;
    }

}
