package org.example.honorsparkingbe.parkingzone.cache;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.ParkingZoneDTO;
import org.example.honorsparkingbe.parkingzone.repository.ParkingZoneRepository;
import org.example.honorsparkingbe.util.RedisUtil;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingZoneCacheManager {

  public static final String PARKING_ZONE_CACHE_NAME = "parkingZoneDTO";
  private final ParkingZoneRepository parkingZoneRepository;
  private final RedisUtil redisUtil;
  private final ObjectMapper objectMapper;

  @CachePut(value = "parkingZoneDTO", key = "#parkingZoneDTO.id")
  public ParkingZoneDTO putParkingZone(ParkingZoneDTO parkingZoneDTO) {
    return parkingZoneDTO;
  }

  @Cacheable(cacheNames = "parkingZoneCount")
  public Long getTotalParkingZoneCount() {
    return parkingZoneRepository.count();
  }

  /**
   * 주차장 ID 목록을 받아 해당 주차장 DTO를 Redis 캐시에서 조회합니다. 만약 해당 ID cache Miss가 발생하면 value는 null이 됩니다.
   *
   * @param parkingZoneIds
   * @return
   */
  public Map<Long, ParkingZoneDTO> parkingZoneDTOMapByCache(List<Long> parkingZoneIds) {
    // 1. Redis 키로 변환
    List<String> keys = parkingZoneIds.stream()
        .map(id -> PARKING_ZONE_CACHE_NAME + "::" + id)
        .toList();

    // 2. mget 실행
    List<Object> rawValues = redisUtil.getByIds(keys);
    for (int i = 0; i < rawValues.size(); i++) {
      Object val = rawValues.get(i);
      System.out.println("index=" + i + ", class=" + (val == null ? "null" : val.getClass()));
      System.out.println("value=" + val);
    }
    // 3. id와 결과 매칭
    Map<Long, ParkingZoneDTO> resultMap = new HashMap<>();
    for (int i = 0; i < parkingZoneIds.size(); i++) {
      Long id = parkingZoneIds.get(i);
      Object raw = (i < rawValues.size()) ? rawValues.get(i) : null;

      if (raw instanceof ParkingZoneDTO) {
        resultMap.put(id, (ParkingZoneDTO) raw);
      } else {
        resultMap.put(id, null);
      }
    }
    return resultMap;
  }
}
