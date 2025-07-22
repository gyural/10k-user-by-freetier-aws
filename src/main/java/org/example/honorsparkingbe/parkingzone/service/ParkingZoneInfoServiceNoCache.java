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
import org.example.honorsparkingbe.favoriteParkingZone.repository.FavoriteParkingZoneRepository;
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
public class ParkingZoneInfoServiceNoCache {


  //Repository
  private final FavoriteParkingZoneRepository favoriteParkingZoneRepository;
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
      List<Long> nonFavoriteParkingZoneIds = parkingZoneRepository.findNormalParkingZoneIdsByDistance(
          request.getLatitude(),
          request.getLongitude(),
          10 - favoriteParkingZoneIds.size(),
          pageable.getPageNumber() * pageable.getPageSize(),
          favoriteParkingZoneIds.isEmpty() ? null : favoriteParkingZoneIds);

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

  // 1 casche 활용 X
  private List<ParkingZoneDTO> getParkingzonesByIds(
      List<Long> totalParkingZoneIds,
      List<Long> favoriteZonesIds) {

    List<ParkingZoneDTO> result = new ArrayList<>();

    List<ParkingZoneEntity> dbResults = parkingZoneRepository.findAllByIdIn(totalParkingZoneIds);
    for (ParkingZoneEntity entity : dbResults) {
      result.add(
          parkingZoneDTOConverter.toDTO(
              entity,
              favoriteZonesIds.contains(entity.getId()), // 즐겨찾기 여부
              parkingFeeRuleDTOConverter.toDtoList(entity.getParkingFeeRuleEntities())
          )
      );
    }
    Map<Long, ParkingZoneDTO> map = result.stream()
        .collect(Collectors.toMap(ParkingZoneDTO::getId, Function.identity()));

    return totalParkingZoneIds.stream()
        .map(map::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }


  // 1. casche 활용 X
  private Long getTotalParkingZoneCount() {
    return parkingZoneRepository.count();
  }

}
