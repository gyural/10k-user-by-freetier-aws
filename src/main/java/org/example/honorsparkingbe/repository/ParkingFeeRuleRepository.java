package org.example.honorsparkingbe.repository;

import org.example.honorsparkingbe.domain.entity.ParkingFeeRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingFeeRuleRepository extends JpaRepository<ParkingFeeRuleEntity, Long> {
    List<ParkingFeeRuleEntity> findAllByParkingZoneEntity_Id(Long parkingZoneId);
}
