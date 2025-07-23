package org.example.honorsparkingbe.parkingzone.cache;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.ParkingZoneDTO;
import org.example.honorsparkingbe.parkingzone.repository.ParkingZoneRepository;
import org.example.honorsparkingbe.util.RedisUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingZoneCacheManager {

  public static final String PARKING_ZONE_CACHE_NAME = "parkingZoneDTO";
  private final ParkingZoneRepository parkingZoneRepository;
  private final RedisUtil redisUtil;
  private final ObjectMapper objectMapper;


  /**
   * Returns the total number of parking zones, using cache for improved performance.
   *
   * @return the total count of parking zones
   */
  @Cacheable(cacheNames = "parkingZoneCount")
  public Long getTotalParkingZoneCount() {
    return parkingZoneRepository.count();
  }


  /**
   * Stores multiple ParkingZoneDTO objects in Redis cache using their IDs as part of the cache key.
   *
   * Each ParkingZoneDTO is mapped to a Redis key formatted as "parkingZoneDTO::<ID>", and all entries are stored in Redis in a single batch operation.
   *
   * @param parkingZoneDTOList the list of ParkingZoneDTO objects to cache
   */
  public void putParkingZoneDTOMap(List<ParkingZoneDTO> parkingZoneDTOList) {
    Map<String, Object> keyValueMap = new HashMap<>();
    parkingZoneDTOList.forEach(
        dto -> keyValueMap.put(PARKING_ZONE_CACHE_NAME + "::" + dto.getId(), dto));

    redisUtil.mset(keyValueMap);
  }

  /**
   * Retrieves a map of parking zone IDs to their corresponding ParkingZoneDTO objects from the Redis cache.
   *
   * For each provided parking zone ID, attempts to fetch the associated ParkingZoneDTO from Redis. If a cache miss occurs or the cached value is not a ParkingZoneDTO, the map entry for that ID will be null.
   *
   * @param parkingZoneIds List of parking zone IDs to retrieve from cache.
   * @return Map of parking zone IDs to ParkingZoneDTO objects, with null values for IDs not found in cache.
   */
  public Map<Long, ParkingZoneDTO> parkingZoneDTOMapByCache(List<Long> parkingZoneIds) {
    // 1. Redis 키로 변환
    List<String> keys = parkingZoneIds.stream()
        .map(id -> PARKING_ZONE_CACHE_NAME + "::" + id)
        .toList();

    // 2. mget 실행
    List<Object> rawValues = redisUtil.getByIds(keys);

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
