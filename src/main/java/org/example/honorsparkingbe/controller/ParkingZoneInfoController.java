package org.example.honorsparkingbe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.request.ParkingZoneListRequest;
import org.example.honorsparkingbe.dto.response.ParkingZoneListResponse;
import org.example.honorsparkingbe.service.ParkingZoneInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/parkingzone")
@RequiredArgsConstructor
public class ParkingZoneInfoController {

    private final ParkingZoneInfoService parkingZoneInfoService;

    @GetMapping("/list")
    public ResponseEntity<ParkingZoneListResponse> getParkingZoneList(@Valid @ModelAttribute ParkingZoneListRequest request) {

        ParkingZoneListResponse response = parkingZoneInfoService.getParkingZones(request);
        return ResponseEntity.ok(response);
    }
}
