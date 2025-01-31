package org.example.honorsparkingbe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parkingzone")
public class ParkingInfoController {

    @GetMapping("/list")
    public ResponseEntity<List<String>> getParkingZoneList(
            @RequestParam("latitude") double latitude, //위도
            @RequestParam("longitude") double longitude, //경도
            @RequestParam("memberID") String memberID
    ) {

        // 임시로 주차 구역 리스트를 반환
        List<String> parkingZones = List.of("Zone A", "Zone B", "Zone C");

        return ResponseEntity.ok(parkingZones);
    }
}
