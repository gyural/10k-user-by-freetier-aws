package org.example.honorsparkingbe.parkingzone.service;

import static org.example.honorsparkingbe.parkingzone.cache.ParkingZoneCacheManager.PARKING_ZONE_CACHE_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.dto.ParkingZoneDTO;
import org.example.honorsparkingbe.dto.request.ParkingZoneListRequest;
import org.example.honorsparkingbe.dto.response.ParkingZoneListResponse;
import org.example.honorsparkingbe.favoriteParkingZone.repository.FavoriteParkingZoneRepository;
import org.example.honorsparkingbe.favoriteParkingZone.service.FavoriteParkingZoneService;
import org.example.honorsparkingbe.parkingzone.cache.ParkingZoneCacheManager;
import org.example.honorsparkingbe.parkingzone.repository.ParkingZoneRepository;
import org.example.honorsparkingbe.util.converter.dto.ParkingFeeRuleDTOConverter;
import org.example.honorsparkingbe.util.converter.dto.ParkingZoneDTOConverter;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParkingZoneInfoService {

  //Repository
  private final FavoriteParkingZoneRepository favoriteParkingZoneRepository;
  private final ParkingZoneRepository parkingZoneRepository;
  //Util
  private final ParkingZoneDTOConverter parkingZoneDTOConverter;
  private final ParkingFeeRuleDTOConverter parkingFeeRuleDTOConverter;
  private final ParkingZoneCacheManager parkingZoneCacheService;
  private final FavoriteParkingZoneService favoriteParkingZoneService;
  private final CacheManager cacheManager;

  @Transactional(readOnly = true)
  public ParkingZoneListResponse getParkingZones(ParkingZoneListRequest request, Long userId) {
    // 1. DTO에서 요구 값 추출
    Pageable pageable = createPageable(request.getPage(), 10);
    // 2. 즐겨찾기된 주차장 ID 목록 불러오기
    List<Long> favoriteZonesIds = getFavoriteParkingZonesID(userId);
    // 3. nonParkingzonesSlots -> 일반 주차장이 필요한 개수(최대 10개 이하, 최소 0개 이상)
    int nonParkingzonesSlots = Math.min(
        10,
        Math.max(0,
            (pageable.getPageNumber() - 1) * pageable.getPageSize() - favoriteZonesIds.size())
    );

    // favoriteZones id offset 만큼제거
    if (nonParkingzonesSlots > 0 && nonParkingzonesSlots <= favoriteZonesIds.size()) {
      favoriteZonesIds = favoriteZonesIds.subList(nonParkingzonesSlots, favoriteZonesIds.size());
    } else if (nonParkingzonesSlots > favoriteZonesIds.size()) {
      favoriteZonesIds = Collections.emptyList();
    }

    // 4. 일반 주차장 ID배열 추출
    List<Long> nonFavoriteZonesIds = getNonFavoriteParkingZones(
        nonParkingzonesSlots, userId, favoriteZonesIds, request, pageable
    );

    // 5. 즐겨찾기 비즐겨찾기 ID 배열 합치기
    List<Long> totalParkingZoneIds = Stream.concat(favoriteZonesIds.stream(),
            nonFavoriteZonesIds.stream())
        .collect(Collectors.toList());
    // 6. 최종 필요 주차장 엔티티 불러오기
    List<ParkingZoneDTO> parkingZoneDTOS = getParkingzonesByIds(
        totalParkingZoneIds,
        favoriteZonesIds
    );

    // 7. page관련 데이터 추출
    Long totalItems = parkingZoneCacheService.getTotalParkingZoneCount(); // 전체 주차장 수
    int totalPages =
        (totalItems.intValue() + pageable.getPageSize() - 1) / pageable.getPageSize(); // 총 페이지 수

    ParkingZoneListResponse.PaginationInfo paginationInfo = ParkingZoneListResponse.PaginationInfo.builder()
        .currentPage(pageable.getPageNumber())
        .totalPages(totalPages)
        .pagePerItem(pageable.getPageSize())
        .totalItems(totalItems.intValue())
        .build();

    // 8. 응답 객체 생성
    return ParkingZoneListResponse.builder()
        .parkingZones(parkingZoneDTOS)
        .pagination(paginationInfo)
        .build();
  }

  private Pageable createPageable(Integer pageNum, int pageSize) {
    return PageRequest.of(pageNum == null ? 0 : pageNum, pageSize);
  }

  private List<Long> getFavoriteParkingZonesID(Long userId) {
    return favoriteParkingZoneService.getFavoriteParkingZoneIds(userId);
  }

  private List<Long> getNonFavoriteParkingZones(
      int remainingSlots, // 주차장의 갯수 제약
      Long userId, //총 favoritezone 개수
      List<Long> favoriteZonesIds, // 제외할 즐겨찾기 주차장 ID들
      ParkingZoneListRequest query, // 필터링에 필요한 파라미터
      Pageable pageable // 페이징 처리
  ) {
    if (remainingSlots <= 0) {
      return Collections.emptyList();
    }
    List<Long> exclusionIds = favoriteZonesIds.isEmpty()
        ? Collections.singletonList(0L)
        : favoriteZonesIds;

    int totalFavoriteCount = favoriteParkingZoneRepository.countByMemberEntity_Id(userId);
    int effectiveOffset = Math.max(0, (pageable.getPageNumber() * 10) - totalFavoriteCount);
    return parkingZoneRepository.findClosestParkingZonesIDWithExclusion(
        query.getLatitude(),
        query.getLongitude(),
        remainingSlots,  // ⭐ 제한 개수 설정
        effectiveOffset,
        exclusionIds
    );
  }

  // 1 casche 활용 X
//  private List<ParkingZoneDTO> getParkingzonesByIds(
//      List<Long> totalParkingZoneIds,
//      List<Long> favoriteZonesIds) {
//
//    List<ParkingZoneDTO> result = new ArrayList<>();
//
//    List<ParkingZoneEntity> dbResults = parkingZoneRepository.findAllByIdIn(totalParkingZoneIds);
//    for (ParkingZoneEntity entity : dbResults) {
//      System.out.println("Saving to cache: " + entity.getId());
//      result.add(
//          parkingZoneCacheService.putParkingZone(
//              parkingZoneDTOConverter.toDTO(
//                  entity,
//                  favoriteZonesIds.contains(entity.getId()), // 즐겨찾기 여부
//                  parkingFeeRuleDTOConverter.toDtoList(entity.getParkingFeeRuleEntities())
//              )
//          )
//      );
//    }
//    Map<Long, ParkingZoneDTO> map = result.stream()
//        .collect(Collectors.toMap(ParkingZoneDTO::getId, Function.identity()));
//
//    return totalParkingZoneIds.stream()
//        .map(map::get)
//        .filter(Objects::nonNull)
//        .collect(Collectors.toList());
//  }

  // 2 casche 활용 0
  private List<ParkingZoneDTO> getParkingzonesByIds(
      List<Long> totalParkingZoneIds,
      List<Long> favoriteZonesIds) {

    List<ParkingZoneDTO> result = new ArrayList<>();
    List<Long> missIds = new ArrayList<>();

    Cache cache = cacheManager.getCache(PARKING_ZONE_CACHE_NAME);

    for (Long id : totalParkingZoneIds) {
      ParkingZoneDTO cached = cache != null ? cache.get(id, ParkingZoneDTO.class) : null;
      if (cached != null) {
        result.add(cached);
      } else {
        missIds.add(id);
      }
    }
    if (!missIds.isEmpty()) {
      List<ParkingZoneEntity> dbResults = parkingZoneRepository.findAllByIdIn(missIds);
      for (ParkingZoneEntity entity : dbResults) {
        result.add(
            parkingZoneCacheService.putParkingZone(
                parkingZoneDTOConverter.toDTO(
                    entity,
                    favoriteZonesIds.contains(entity.getId()), // 즐겨찾기 여부
                    parkingFeeRuleDTOConverter.toDtoList(entity.getParkingFeeRuleEntities())
                )
            )
        );
      }
    }

    Map<Long, ParkingZoneDTO> map = result.stream()
        .collect(Collectors.toMap(ParkingZoneDTO::getId, Function.identity()));

    return totalParkingZoneIds.stream()
        .map(map::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }


}

