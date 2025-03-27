package org.example.honorsparkingbe.controller;

import static org.example.honorsparkingbe.security.util.SecurityUtil.getCurrentUserId;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.request.ParkingHistoryRequest;
import org.example.honorsparkingbe.dto.response.ParkingHistoryResponse;
import org.example.honorsparkingbe.service.ParkingHistoryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.dto.DeleteParkingHistoryDTO;
import org.example.honorsparkingbe.dto.request.ParkingHistoryDeleteRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.example.honorsparkingbe.service.ParkingHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parking/history")
@RequiredArgsConstructor
public class ParkingHistoryController {

  private final ParkingHistoryService parkingHistoryService;


@GetMapping
public ResponseEntity<ParkingHistoryResponse> getParkingHistory(@Valid @ModelAttribute ParkingHistoryRequest request) {

    ParkingHistoryResponse response = parkingHistoryService.getParkingHistory(request);
    return ResponseEntity.ok(response);
}

  @DeleteMapping
  public ResponseEntity<?> deleteParkingHistory(
      @Valid @RequestBody ParkingHistoryDeleteRequest request
  ) {

    Long userId = getCurrentUserId();
    
    return ResponseEntity.ok(
        parkingHistoryService.softDeleteParkingHistories(
            DeleteParkingHistoryDTO.builder()
                .paringHistoryDeleteRequest(request)
                .userId(userId)
                .build()
        ));
  }

}
