package org.example.honorsparkingbe.repository;

import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ParkingHistoryRepository extends JpaRepository<ParkingHistoryEntity, Long> {

    @Query("SELECT p FROM ParkingHistoryEntity p WHERE p.entranceTime BETWEEN :startTime AND :endTime")
    Page<ParkingHistoryEntity> findByEntranceTimeBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );

}
