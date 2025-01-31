package org.example.honorsparkingbe.repository;

import org.example.honorsparkingbe.domain.entity.FavoriteParkingZoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteParkingZoneRepository extends JpaRepository<FavoriteParkingZoneEntity, Long> {

    List<FavoriteParkingZoneEntity> findAllByMemberEntity_Id(Long memberId);
}
