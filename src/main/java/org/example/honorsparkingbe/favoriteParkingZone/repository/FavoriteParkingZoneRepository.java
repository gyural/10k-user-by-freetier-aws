package org.example.honorsparkingbe.favoriteParkingZone.repository;

import java.util.Set;
import org.example.honorsparkingbe.domain.entity.FavoriteParkingZoneEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FavoriteParkingZoneRepository extends
    JpaRepository<FavoriteParkingZoneEntity, Long> {

  @EntityGraph(attributePaths = {"parkingZoneEntity.id"})
  @Query("""
          SELECT f 
          FROM FavoriteParkingZoneEntity f 
          WHERE f.memberEntity.id = :memberId
          ORDER BY f.id ASC
      """)
  Page<FavoriteParkingZoneEntity> findAllByMemberEntity_IdOrderByIdAsc(
      @Param("memberId") Long memberId, Pageable pageable);

  @Transactional
  @Modifying
  @Query("DELETE FROM FavoriteParkingZoneEntity f WHERE f.memberEntity.id = :memberId AND f.parkingZoneEntity.id = :parkingZoneId")
  int deleteByMemberEntity_IdAndParkingZoneEntity_Id(Long memberId, Long parkingZoneId);

  int countByMemberEntity_Id(Long memberId);

  @EntityGraph(attributePaths = {"parkingZoneEntity.id"})
  Set<FavoriteParkingZoneEntity> findAllByMemberEntity_Id(Long memberId);

  /**
       * Retrieves a paginated list of IDs for favorite parking zone entries associated with the specified member, ordered by ID in ascending order.
       *
       * @param memberId the ID of the member whose favorite parking zone entry IDs are to be retrieved
       * @param pageable pagination information for the query results
       * @return a page of favorite parking zone entry IDs for the given member
       */
      @EntityGraph(attributePaths = {"parkingZoneEntity.id"})
  @Query("SELECT f.id FROM FavoriteParkingZoneEntity f WHERE f.memberEntity.id = :memberId ORDER BY f.id ASC")
  Page<Long> findAllIdsByMemberEntity_IdOrderByIdAsc(@Param("memberId") Long memberId,
      Pageable pageable);
}
