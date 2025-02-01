package org.example.honorsparkingbe.repository;

import org.example.honorsparkingbe.domain.entity.FavoriteParkingZoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteParkingZoneRepository extends JpaRepository<FavoriteParkingZoneEntity, Long> {

    List<FavoriteParkingZoneEntity> findAllByMemberEntity_IdOrderByIdAsc(Long memberId);
}
