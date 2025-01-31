package org.example.honorsparkingbe.repository;

import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParkingZoneRepository extends JpaRepository<ParkingZoneEntity, Long> {
    // ID 배열로 주차장 리스트 반환
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
        LIMIT :limit
    """, nativeQuery = true)
    List<ParkingZoneEntity> findClosestParkingZones(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("limit") int limit
    );
}