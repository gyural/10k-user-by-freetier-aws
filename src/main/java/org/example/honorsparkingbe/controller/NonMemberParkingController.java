package org.example.honorsparkingbe.controller;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.request.NonMemberParkingRequest;
import org.example.honorsparkingbe.dto.response.NonMemberParkingResponse;
import org.example.honorsparkingbe.service.NonMemberParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/parking/nonmember")
@RequiredArgsConstructor
public class NonMemberParkingController {

    private final NonMemberParkingService nonMemberParkingService;

    /**
     * 차량번호를 입력받아 현재 주차 중인 차량인지 확인 후 응답 반환
     * @param request 차량번호 입력 DTO
     * @return 주차 상태 응답
     */
    @PostMapping
    public ResponseEntity<NonMemberParkingResponse> checkNonMemberParking(@RequestBody NonMemberParkingRequest request) {
        return ResponseEntity.ok(nonMemberParkingService.getParkingStatus(request.getVehicleNumber()));
    }
}
// 2