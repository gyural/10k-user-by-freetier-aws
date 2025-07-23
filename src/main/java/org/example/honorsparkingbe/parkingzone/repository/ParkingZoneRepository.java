package org.example.honorsparkingbe.parkingzone.repository;

import java.util.List;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParkingZoneRepository extends JpaRepository<ParkingZoneEntity, Long> {

  boolean existsByZoneName(String name);

  // ID 배열로 주차장 리스트 반환
  @EntityGraph(attributePaths = {"cityEntity", "districtEntity", "eupMyeonDongEntity",
      "parkingFeeRuleEntities"})
  List<ParkingZoneEntity> findAllByIdIn(List<Long> ids);

  // 위경도 기준으로 가까운 거리순으로 정렬된 주차장 리스트 반환
  @Query(value = """
          SELECT p.*,
                 (6371 * acos(
                     cos(radians(:latitude)) * cos(radians(p.latitude)) *
                     cos(radians(p.longitude) - radians(:longitude)) +
                     sin(radians(:latitude)) * sin(radians(p.latitude))
                 )) AS distance
          FROM parkingZone p
          ORDER BY distance ASC
          LIMIT :limit OFFSET :offset
      """, nativeQuery = true)
  List<ParkingZoneEntity> findClosestParkingZones(
      @Param("latitude") double latitude,
      @Param("longitude") double longitude,
      @Param("limit") int limit,
      @Param("offset") int offset
  );

  @Query(value = """
          SELECT p.id,
                 (6371 * acos(
                     cos(radians(:latitude)) * cos(radians(p.latitude)) *
                     cos(radians(p.longitude) - radians(:longitude)) +
                     sin(radians(:latitude)) * sin(radians(p.latitude))
                 )) AS distance
          FROM parkingZone p
          WHERE p.id NOT IN :excludeIds  -- 제외할 parkingZone id를 지정
          ORDER BY distance ASC
          LIMIT :limit OFFSET :offset
      """, nativeQuery = true)
  List<Long> findClosestParkingZonesIDWithExclusion(
      @Param("latitude") double latitude,
      @Param("longitude") double longitude,
      @Param("limit") int limit,
      @Param("offset") int offset,
      @Param("excludeIds") List<Long> excludeIds);

  @EntityGraph(attributePaths = {
      "cityEntity",
      "districtEntity",
      "eupMyeonDongEntity",
      "parkingFeeRuleEntities"
  })
  @Query("""
          SELECT p FROM ParkingZoneEntity p
          WHERE LOWER(p.zoneName) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(p.cityEntity.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(p.districtEntity.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(p.eupMyeonDongEntity.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      """)
  Page<ParkingZoneEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

  /**
   * Counts the number of parking zones whose zone name or related city, district, or eupMyeonDong names contain the specified keyword, case-insensitively.
   *
   * @param keyword the search keyword to match against zone and location names
   * @return the count of matching parking zones
   */
  @Query("""
          SELECT COUNT(p) FROM ParkingZoneEntity p
          WHERE LOWER(p.zoneName) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(p.cityEntity.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(p.districtEntity.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(p.eupMyeonDongEntity.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
      """)
  Long countByKeyword(@Param("keyword") String keyword);

  /**
   * Retrieves a list of parking zone IDs ordered by geographic distance from the specified latitude and longitude.
   *
   * The result is paginated using the provided limit and offset. Optionally excludes parking zones whose IDs are in the given exclusion list; if the exclusion list is null, no IDs are excluded.
   *
   * @param latitude the latitude coordinate to measure distance from
   * @param longitude the longitude coordinate to measure distance from
   * @param limit the maximum number of IDs to return
   * @param offset the number of results to skip before starting to collect the return list
   * @param excludeIds a list of parking zone IDs to exclude from the results, or null to include all
   * @return a list of parking zone IDs sorted by ascending distance from the specified coordinates
   */
  @Query(value = """
      SELECT p.id
      FROM parkingZone p
      WHERE (:excludeIds IS NULL OR p.id NOT IN (:excludeIds))
      ORDER BY (6371 * acos(
          cos(radians(:latitude)) * cos(radians(p.latitude)) *
          cos(radians(p.longitude) - radians(:longitude)) +
          sin(radians(:latitude)) * sin(radians(p.latitude))
      )) ASC
      LIMIT :limit OFFSET :offset
      """, nativeQuery = true)
  List<Long> findNormalParkingZoneIdsByDistance(
      @Param("latitude") double latitude,
      @Param("longitude") double longitude,
      @Param("limit") int limit,
      @Param("offset") int offset,
      @Param("excludeIds") List<Long> excludeIds
  );
}

