package org.example.honorsparkingbe.repository;

import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingHistoryRepository extends JpaRepository<ParkingHistoryEntity, Long> {
    ParkingHistoryEntity findFirstByMemberEntityIdOrderByEntranceTimeDesc(Long memberId);
}
