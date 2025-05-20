package org.example.honorsparkingbe.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.honorsparkingbe.dto.request.NonMemberParkingRequest;
import org.example.honorsparkingbe.dto.request.SyncNonMemberInoutRequest;
import org.example.honorsparkingbe.dto.response.NonMemberParkingListResponse;
import org.example.honorsparkingbe.dto.response.NonMemberParkingResponse;
import org.example.honorsparkingbe.dto.response.SyncNonMemberInoutListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

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
    public SyncNonMemberInoutListResponse getInoutByVehicleNumber(NonMemberParkingRequest request) {
        // 요청 DTO 구성
        SyncNonMemberInoutRequest syncRequest = SyncNonMemberInoutRequest.builder()
                .vehicleNumber(request.getVehicleNumber())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(apiHeaderName, apiKey);

        HttpEntity<SyncNonMemberInoutRequest> entity = new HttpEntity<>(syncRequest, headers);

        try {
            ResponseEntity<SyncNonMemberInoutListResponse> response = restTemplate.postForEntity(
                    syncServerUrl, entity, SyncNonMemberInoutListResponse.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("싱크 서버 요청 중 오류 발생 (차량번호: {}): {}", request.getVehicleNumber(), e.getMessage(), e);
            throw new RuntimeException("싱크 서버 요청 실패", e);
        }
    }

    @Override
    public NonMemberParkingListResponse getParkingStatus(String vehicleNumber) {
        SyncNonMemberInoutListResponse syncResponse = getInoutByVehicleNumber(
                NonMemberParkingRequest.builder()
                        .vehicleNumber(vehicleNumber)
                        .build()
        );

        if (syncResponse != null && syncResponse.getParkingEntries() != null) {
            List<NonMemberParkingResponse> resultList = syncResponse.getParkingEntries().stream()
                    .map(entry -> NonMemberParkingResponse.builder()
                            .vehicleNumber(entry.getVehicleNumber())
                            .parkingLotLocation(entry.getParkingLotLocation())
                            .entryTime(entry.getEntryTime())
                            .totalParkingMinutes(entry.getTotalParkingMinutes())
                            .currentFee(entry.getCurrentFee())
                            .entryPhotoUrl(entry.getEntryPhotoUrl())
                            .build())
                    .toList();

            return NonMemberParkingListResponse.builder()
                    .parkingEntries(resultList)
                    .build();
        }

        return NonMemberParkingListResponse.builder()
                .parkingEntries(Collections.emptyList())
                .build();
    }
}
