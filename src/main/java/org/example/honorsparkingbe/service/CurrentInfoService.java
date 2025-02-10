package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.repository.ParkingHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrentInfoService {

    private final ParkingHistoryRepository parkingHistoryRepository;

    public CurrentInfoService(ParkingHistoryRepository parkingHistoryRepository) {
        this.parkingHistoryRepository = parkingHistoryRepository;
    }

    public Map<String, Object> getCurrentParkingInfo(Long memberId) {

        // ParkingHistory에서 해당 memberId를 가진 가장 최신의 기록 가져오기
        ParkingHistoryEntity latestHistory = parkingHistoryRepository.findFirstByMemberEntityIdOrderByEntranceTimeDesc(memberId);



        // 디버깅용 로그 출력
        System.out.println("Latest Parking History: " + latestHistory);

        Map<String, Object> response = new HashMap<>();

        if (latestHistory == null) {
            response.put("message", "해당 사용자의 주차 기록이 없습니다.");
            return response;
        }

        // 가져온 주차 내역의 모든 정보 출력
        System.out.println("Parking History ID: " + latestHistory.getId());
        System.out.println("Car ID: " + (latestHistory.getCarEntity() != null ? latestHistory.getCarEntity().getId() : "NULL"));
        System.out.println("Member ID: " + (latestHistory.getMemberEntity() != null ? latestHistory.getMemberEntity().getId() : "NULL"));
        System.out.println("Parking Zone ID: " + (latestHistory.getParkingZoneEntity() != null ? latestHistory.getParkingZoneEntity().getId() : "NULL"));
        System.out.println("Entrance Time: " + latestHistory.getEntranceTime());
        System.out.println("Exit Time: " + latestHistory.getExitTime());
        System.out.println("Payment Type: " + latestHistory.getPaymentType());

        // ParkingZone 정보 확인
        ParkingZoneEntity parkingZone = latestHistory.getParkingZoneEntity();
        if (parkingZone != null) {
            System.out.println("Parking Zone Name: " + parkingZone.getZoneName());
        } else {
            System.out.println("Parking Zone is NULL!");
        }

        // 그대로 반환하여 JSON 응답 확인
        response.put("parkingHistory", latestHistory);
        return response;
    }
}

