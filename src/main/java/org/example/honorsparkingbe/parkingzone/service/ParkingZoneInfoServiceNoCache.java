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
   * Retrieves a paginated list of parking zones for a user, prioritizing favorite zones and supplementing with nearby non-favorite zones as needed.
   *
   * The method returns parking zone details along with pagination metadata. Favorite parking zones are listed first, followed by additional zones based on proximity to the provided latitude and longitude, ensuring the total matches the page size.
   *
   * @param request Contains pagination and location information for the parking zone query.
   * @param userId The ID of the user requesting parking zone information.
   * @return A response containing the list of parking zones and pagination details.
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

  /**
   * Creates a {@link Pageable} object for pagination, defaulting to the first page if the page number is null.
   *
   * @param pageNum  the page number to retrieve, or null to default to page 0
   * @param pageSize the number of items per page
   * @return a {@link Pageable} instance for the specified page and size
   */
  private Pageable createPageable(Integer pageNum, int pageSize) {
    return PageRequest.of(pageNum == null ? 0 : pageNum, pageSize);
  }

  /**
   * Retrieves a list of ParkingZoneDTOs for the specified parking zone IDs, marking which zones are favorites and including their parking fee rules.
   *
   * The returned list preserves the order of the input ID list and excludes any IDs not found in the database.
   *
   * @param totalParkingZoneIds the list of parking zone IDs to retrieve, in desired order
   * @param favoriteZonesIds the list of parking zone IDs that are marked as favorites
   * @return a list of ParkingZoneDTOs corresponding to the input IDs, with favorite status and fee rules populated
   */
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


  /**
   * Retrieves the total number of parking zones from the repository.
   *
   * @return the total count of parking zones
   */
  private Long getTotalParkingZoneCount() {
    return parkingZoneRepository.count();
  }

}
