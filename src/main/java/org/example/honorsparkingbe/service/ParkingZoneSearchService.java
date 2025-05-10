package org.example.honorsparkingbe.service;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.GetParkingZoneByKeywordDTO;
import org.example.honorsparkingbe.dto.response.ParkingZoneSearchResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingZoneSearchService {

  public ParkingZoneSearchResponse getParkingZonesByKeyword(GetParkingZoneByKeywordDTO builder) {

    return ParkingZoneSearchResponse.builder().build();
  }
}
