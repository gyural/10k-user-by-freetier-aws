package org.example.honorsparkingbe.dto.response;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ParkingZoneListResponse {
    private List<ParkingZoneInfo> parkingZone;
    private PaginationInfo pagination;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParkingZoneInfo {
        private boolean isFavorite;
        private double latitude;
        private double longitude;
        private String zoneName;
        private String cityName;
        private String districtName;
        private String eupMyeonDongName;
        private int electricCarSpaceCount;
        private boolean isReservedOk;
        private int size;
        private List<Integer> floor;
        private int maxCost;
        private int hourlyRate;
        private int minuteRate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaginationInfo {
        private int currentPage;
        private int totalPages;
        private int pageSize;
        private long totalItems;
    }
}
