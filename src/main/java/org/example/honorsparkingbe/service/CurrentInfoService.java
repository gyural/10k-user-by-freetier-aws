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

        Map<String, Object> response = new HashMap<>();

        // 아예 이용 기록이 없는 경우
        if (latestHistory == null) {
            response.put("message", "해당 사용자의 주차 기록이 없습니다.");
            return response;
        }

        // exit time이 있는 경우 : 현재 입차된 상태 X
        if(latestHistory.getExitTime()!=null){
            response.put("isParked", false);
            response.put("parkingZone", null);
            response.put("entranceTime", null);
            response.put("cost", 0);
            response.put("message", "현재 주차 중인 상태가 아닙니다.");
            return response;
        }

        // exitTime이 없는 경우 : 현재 주차 중
        ParkingZoneEntity parkingZone = latestHistory.getParkingZoneEntity();


        /**
         * 1. parkingHistory(latesHistory)에서 parkingZoneId를 찾는다.
         * 1-2. 해당 parkingZoneId를 이용하여 parkingFeeRule에 들어가 해당되는 값을 찾는다.
         * 1-3. 아마 여러 튜플을 가져올 것으로 예상됨
         *
         * 2. parkingHistory(latesHistory)에서 carId를 찾아 해당하는 자동차의 carType을 찾아온다.
         * 2-2. 아까 가져온 여러 튜플들 중 carType이 일치하지 않는 것은 제외한다.
         *
         * 3. 남은 튜플들을 해당 페이지에서 sysout을 확인
         */



        // JSON 응답 형식 맞추기
        Map<String, Object> parkingZoneInfo = new HashMap<>();
        parkingZoneInfo.put("zoneName", parkingZone.getZoneName());
        parkingZoneInfo.put("hourlyRate", null); // 요금 정보는 현재 설정되지 않음
        parkingZoneInfo.put("entranceTime", latestHistory.getEntranceTime());
        parkingZoneInfo.put("cost", null); // 요금 정보는 현재 설정되지 않음

        response.put("parkingZone", parkingZoneInfo);
        return response;


    }
}

