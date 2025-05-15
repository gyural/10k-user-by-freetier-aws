package org.example.honorsparkingbe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.GetParkingZoneByKeywordDTO;
import org.example.honorsparkingbe.dto.request.ParkingZoneSearchRequest;
import org.example.honorsparkingbe.dto.response.ParkingZoneSearchResponse;
import org.example.honorsparkingbe.security.util.SecurityUtil;
import org.example.honorsparkingbe.service.ParkingZoneSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class ParkingZoneSearchController {

  private final ParkingZoneSearchService parkingZoneSearchService;


  @GetMapping("/parking")
  public ResponseEntity<ParkingZoneSearchResponse> searchParkingZoneByKeyword(
      @ModelAttribute @Valid ParkingZoneSearchRequest request) {

    return ResponseEntity.ok(parkingZoneSearchService.getParkingZonesByKeyword(
        GetParkingZoneByKeywordDTO.builder()
            .keyword(request.getKeyword())
            .memberId(SecurityUtil.getCurrentUserId())
            .page(request.getPage())
            .build()));
  }
}
