package org.example.honorsparkingbe.parkingzone.cache;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.ParkingZoneDTO;
import org.example.honorsparkingbe.parkingzone.repository.ParkingZoneRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingZoneCacheManager {

  public static final String PARKING_ZONE_CACHE_NAME = "parkingZoneDTO";
  private final ParkingZoneRepository parkingZoneRepository;

  @CachePut(value = "parkingZoneDTO", key = "#parkingZoneDTO.id")
  public ParkingZoneDTO putParkingZone(ParkingZoneDTO parkingZoneDTO) {

    return parkingZoneDTO;
  }

  @Cacheable(cacheNames = "parkingZoneCount")
  public Long getTotalParkingZoneCount() {
    return parkingZoneRepository.count();
  }
}
