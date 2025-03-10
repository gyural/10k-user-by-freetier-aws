package org.example.honorsparkingbe.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.request.ParkingHistoryRequest;
import org.example.honorsparkingbe.dto.request.ParkingZoneListRequest;
import org.example.honorsparkingbe.dto.response.ParkingHistoryResponse;
import org.example.honorsparkingbe.service.ParkingHistoryService;
import org.example.honorsparkingbe.service.ParkingZoneInfoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.example.honorsparkingbe.security.CustomUserDetails;


@RestController
@RequestMapping("/api/v1/parking")
@RequiredArgsConstructor

public class ParkingHistoryController {

    private final ParkingHistoryService parkingHistoryService;

    @GetMapping("/history")
    public ResponseEntity<ParkingHistoryResponse> getParkingHistory(@Valid @ModelAttribute ParkingHistoryRequest request) {

        ParkingHistoryResponse response = parkingHistoryService.getParkingHistory(request);
        return ResponseEntity.ok(response);
    }
}



