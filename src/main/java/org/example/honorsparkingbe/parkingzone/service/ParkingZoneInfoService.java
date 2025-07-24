package org.example.honorsparkingbe.parkingzone.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.dto.ParkingZoneDTO;
import org.example.honorsparkingbe.dto.request.ParkingZoneListRequest;
import org.example.honorsparkingbe.dto.response.ParkingZoneListResponse;
import org.example.honorsparkingbe.favoriteParkingZone.service.FavoriteParkingZoneService;
import org.example.honorsparkingbe.parkingzone.cache.ParkingZoneCacheManager;
import org.example.honorsparkingbe.parkingzone.repository.ParkingZoneRepository;
import org.example.honorsparkingbe.util.converter.dto.ParkingFeeRuleDTOConverter;
import org.example.honorsparkingbe.util.converter.dto.ParkingZoneDTOConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParkingZoneInfoService {

  //Repository
  private final ParkingZoneRepository parkingZoneRepository;
  //Util
  private final ParkingZoneDTOConverter parkingZoneDTOConverter;
  private final ParkingFeeRuleDTOConverter parkingFeeRuleDTOConverter;
  private final ParkingZoneCacheManager parkingZoneCacheService;
  private final FavoriteParkingZoneService favoriteParkingZoneService;
  private final ParkingZoneCacheManager parkingZoneCacheManager;

  /**
   * 주차장 리스트를 불러오는 서비스
   *
   * @param request DTO에서 필요한 값 추출
   * @param userId  현재 로그인한 유저 ID
   * @return ParkingZoneListResponse
   */
  @Transactional(readOnly = true)
  public ParkingZoneListResponse getParkingZones(ParkingZoneListRequest request, Long userId) {

    Pageable pageable = createPageable(request.getPage(), 10);

    // 1. 전체 주차장 ID 목록 (favoriteParkingZoneIds + nonFavoriteParkingZoneIds)
    List<Long> favoriteParkingZoneIds = favoriteParkingZoneService.getFavoriteParkingZoneIds(userId,
        pageable);
    List<Long> targetParkingZoneIds = new ArrayList<>(favoriteParkingZoneIds);
    if (favoriteParkingZoneIds.size() < pageable.getPageSize() /*10*/) {
      List<Long> nonFavoriteParkingZoneIds =
          parkingZoneRepository.findNormalParkingZoneIdsByDistanceWithExclusion(
              request.getLatitude(),
              request.getLongitude(),
              10 - favoriteParkingZoneIds.size(),
              pageable.getPageNumber() * pageable.getPageSize(),
              favoriteParkingZoneIds.isEmpty() ? null : favoriteParkingZoneIds);

      targetParkingZoneIds.addAll(nonFavoriteParkingZoneIds);
    } else if (!favoriteParkingZoneIds.isEmpty()) {
      List<Long> nonFavoriteParkingZoneIds =
          parkingZoneRepository.findNormalParkingZoneIdsByDistance(
              request.getLatitude(),
              request.getLongitude(),
              10 - favoriteParkingZoneIds.size(),
              pageable.getPageNumber() * pageable.getPageSize()
          );

      targetParkingZoneIds.addAll(nonFavoriteParkingZoneIds);
    }

    // 2. 최종 필요 주차장 엔티티 불러오기
    List<ParkingZoneDTO> parkingZoneDTOS = getParkingzonesByIds(
        targetParkingZoneIds,
        favoriteParkingZoneIds);

    // 3. page관련 데이터 추출
    Long totalItems = getTotalParkingZoneCount(); // 전체 주차장 수
    int totalPages =
        (totalItems.intValue() + pageable.getPageSize() - 1) / pageable.getPageSize(); // 총 페이지 수

    ParkingZoneListResponse.PaginationInfo paginationInfo = ParkingZoneListResponse.PaginationInfo.builder()
        .currentPage(pageable.getPageNumber())
        .totalPages(totalPages)
        .pagePerItem(pageable.getPageSize())
        .totalItems(totalItems.intValue())
        .build();

    return ParkingZoneListResponse.builder()
        .parkingZones(parkingZoneDTOS)
        .pagination(paginationInfo)
        .build();
  }

  private Pageable createPageable(Integer pageNum, int pageSize) {
    return PageRequest.of(pageNum == null ? 0 : pageNum, pageSize);
  }

  private List<ParkingZoneDTO> getParkingzonesByIds(
      List<Long> totalParkingZoneIds,
      List<Long> favoriteZonesIds) {

    List<ParkingZoneDTO> result = new ArrayList<>();
    List<Long> missIds = new ArrayList<>();

    // casche에서 조회 및 result 저장
    Map<Long, ParkingZoneDTO> cachedParkingZones = parkingZoneCacheManager
        .parkingZoneDTOMapByCache(totalParkingZoneIds);
    for (Long id : cachedParkingZones.keySet()) {
      if (cachedParkingZones.get(id) == null) {
        missIds.add(id);
      } else {
        result.add(cachedParkingZones.get(id));
      }
    }

    // missIds DB 조회
    if (!missIds.isEmpty()) {
      List<ParkingZoneEntity> dbResults = parkingZoneRepository.findAllByIdIn(missIds);

      List<ParkingZoneDTO> missedParkingZoneDTOs = dbResults.stream().map(el ->
          parkingZoneDTOConverter.toDTO(
              el,
              favoriteZonesIds.contains(el.getId()), // 즐겨찾기 여부
              parkingFeeRuleDTOConverter.toDtoList(el.getParkingFeeRuleEntities())
          )).toList();
      result.addAll(missedParkingZoneDTOs);

      parkingZoneCacheManager.putParkingZoneDTOMap(missedParkingZoneDTOs);
    }

    Map<Long, ParkingZoneDTO> map = result.stream()
        .collect(Collectors.toMap(ParkingZoneDTO::getId, Function.identity()));

    return totalParkingZoneIds.stream()
        .map(map::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private Long getTotalParkingZoneCount() {
    return parkingZoneCacheService.getTotalParkingZoneCount();
  }

}

