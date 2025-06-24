package org.example.honorsparkingbe.parkingzone.cache;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.ParkingZoneDTO;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingZoneCacheManager {

  public static final String PARKING_ZONE_CACHE_NAME = "parkingZoneDTO";

  @CachePut(value = "parkingZoneDTO", key = "#parkingZoneDTO.id")
  public ParkingZoneDTO putParkingZone(ParkingZoneDTO parkingZoneDTO) {

    return parkingZoneDTO;
  }
}
