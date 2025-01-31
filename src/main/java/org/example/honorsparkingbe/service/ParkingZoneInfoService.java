package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.dto.request.ParkingZoneListRequest;
import org.example.honorsparkingbe.dto.response.ParkingZoneListResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingZoneInfoService {
    public ParkingZoneListResponse getParkingZones(ParkingZoneListRequest request) {
        // 더미 데이터
        List<ParkingZoneListResponse.ParkingZoneInfo> parkingZones = List.of(
                ParkingZoneListResponse.ParkingZoneInfo.builder()
                        .isFavorite(true)
                        .latitude(37.5665)
                        .longitude(126.9780)
                        .zoneName("서울시청 주차장")
                        .cityName("서울특별시")
                        .districtName("중구")
                        .eupMyeonDongName("태평로1가")
                        .electricCarSpaceCount(5)
                        .isReservedOk(true)
                        .size(100)
                        .floor(List.of(1, 2, -1))
                        .maxCost(20000)
                        .hourlyRate(3000)
                        .minuteRate(500)
                        .build(),
                ParkingZoneListResponse.ParkingZoneInfo.builder()
                        .isFavorite(false)
                        .latitude(37.5796)
                        .longitude(126.9770)
                        .zoneName("경복궁 주차장")
                        .cityName("서울특별시")
                        .districtName("종로구")
                        .eupMyeonDongName("세종로")
                        .electricCarSpaceCount(3)
                        .isReservedOk(false)
                        .size(50)
                        .floor(List.of(1, -1))
                        .maxCost(15000)
                        .hourlyRate(2000)
                        .minuteRate(300)
                        .build()
        );

        // 페이지네이션 정보
        ParkingZoneListResponse.PaginationInfo pagination = ParkingZoneListResponse.PaginationInfo.builder()
                .currentPage(1)
                .totalPages(5)
                .pageSize(2)
                .totalItems(10)
                .build();

        return ParkingZoneListResponse.builder()
                .parkingZone(parkingZones)
                .pagination(pagination)
                .build();
    }
}
