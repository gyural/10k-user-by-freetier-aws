package org.example.honorsparkingbe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.honorsparkingbe.dto.request.NonMemberParkingRequest;
import org.example.honorsparkingbe.dto.request.SyncInoutRequest;
import org.example.honorsparkingbe.dto.request.SyncInoutRequest.Inout;
import org.example.honorsparkingbe.dto.response.NonMemberParkingResponse;
import org.example.honorsparkingbe.dto.response.SyncInoutResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class NonMemberParkingServiceImpl implements NonMemberParkingService {

    private final RestTemplate restTemplate;

    @Value("${sync.url}")
    private String syncServerUrl;

    @Value("${sync.api-key}")
    private String apiKey;

    @Value("${sync.header-name}")
    private String apiHeaderName;

    @Override
    public SyncInoutResponse getInoutByVehicleNumber(NonMemberParkingRequest request) {
        // 요청 본문 구성
        Inout inout = Inout.builder()
                .vehicleNumber(request.getVehicleNumber())
                .entryId(null)
                .entryTime(null)
                .parkinglotId(null)
                .build();

        SyncInoutRequest syncRequest = SyncInoutRequest.builder()
                .inoutList(Collections.singletonList(inout))
                .build();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(apiHeaderName, apiKey);

        HttpEntity<SyncInoutRequest> entity = new HttpEntity<>(syncRequest, headers);

        try {
            ResponseEntity<SyncInoutResponse> response = restTemplate.postForEntity(
                    syncServerUrl, entity, SyncInoutResponse.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("싱크 서버 요청 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("싱크 서버 요청 실패", e);
        }
    }

    @Override
    public NonMemberParkingResponse getParkingStatus(String vehicleNumber) {
        // 위의 메서드 재사용
        SyncInoutResponse syncResponse = getInoutByVehicleNumber(
                NonMemberParkingRequest.builder()
                        .vehicleNumber(vehicleNumber)
                        .build()
        );

        boolean isParked = syncResponse != null &&
                syncResponse.getValidNonExitEntries() != null &&
                !syncResponse.getValidNonExitEntries().isEmpty();

        return NonMemberParkingResponse.builder()
                .isParked(isParked)
                .message(isParked ? "현재 주차 중입니다." : "주차 기록이 없습니다.")
                .build();
    }
}
  //6 end