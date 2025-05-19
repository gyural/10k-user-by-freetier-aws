package org.example.honorsparkingbe.controller;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.response.NonMemberParkingListResponse;
import org.example.honorsparkingbe.service.NonMemberParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/parking")
@RequiredArgsConstructor
public class NonMemberParkingController {

    private final NonMemberParkingService nonMemberParkingService;

    /**
     * 차량번호를 입력받아 현재 주차 중인 차량인지 확인 후 응답 반환
     * @param vehicleNumber
     * @return
     */
    @GetMapping("/nonmember")
    public ResponseEntity<NonMemberParkingListResponse> getParkingStatus(@RequestParam String vehicleNumber) {
        return ResponseEntity.ok(nonMemberParkingService.getParkingStatus(vehicleNumber));
    }
}

// 2