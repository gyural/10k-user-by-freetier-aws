package org.example.honorsparkingbe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.honorsparkingbe.dto.request.NonMemberParkingRequest;
import org.example.honorsparkingbe.dto.request.SyncNonMemberInoutRequest;
import org.example.honorsparkingbe.dto.response.NonMemberParkingResponse;
import org.example.honorsparkingbe.dto.response.SyncNonMemberInoutResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NonMemberParkingServiceImpl implements NonMemberParkingService {

    private final RestTemplate restTemplate;

    @Value("${sync.url}")
    private String syncServerUrl;

    @Value("${api.key}")
    private String apiKey;

    @Value("${sync.header-name}")
    private String apiHeaderName;

    @Override
    public SyncNonMemberInoutResponse getInoutByVehicleNumber(NonMemberParkingRequest request) {
        // 요청 DTO 구성
        SyncNonMemberInoutRequest syncRequest = SyncNonMemberInoutRequest.builder()
                .vehicleNumber(request.getVehicleNumber())
                .build();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(apiHeaderName, apiKey);

        HttpEntity<SyncNonMemberInoutRequest> entity = new HttpEntity<>(syncRequest, headers);

        try {
            ResponseEntity<SyncNonMemberInoutResponse> response = restTemplate.postForEntity(
                    syncServerUrl, entity, SyncNonMemberInoutResponse.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("싱크 서버 요청 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("싱크 서버 요청 실패", e);
        }
    }

    @Override
    public NonMemberParkingResponse getParkingStatus(String vehicleNumber) {
        SyncNonMemberInoutResponse response = getInoutByVehicleNumber(
                NonMemberParkingRequest.builder()
                        .vehicleNumber(vehicleNumber)
                        .build()
        );

        if (response != null && response.getEntryTime() != null) {
            return NonMemberParkingResponse.builder()
                    .vehicleNumber(response.getVehicleNumber())
                    .parkingLotLocation(response.getParkingLotLocation())
                    .entryTime(response.getEntryTime())
                    .totalParkingMinutes(response.getTotalParkingMinutes())
                    .currentFee(response.getCurrentFee())
                    .entryPhotoUrl(response.getEntryPhotoUrl())
                    .build();
        }

        return NonMemberParkingResponse.builder()
                .vehicleNumber(vehicleNumber)
                .parkingLotLocation(null)
                .entryTime(null)
                .totalParkingMinutes(0)
                .currentFee(0)
                .entryPhotoUrl(null)
                .build();
    }
}
