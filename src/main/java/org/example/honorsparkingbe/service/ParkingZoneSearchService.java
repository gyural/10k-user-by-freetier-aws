package org.example.honorsparkingbe.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.dto.GetParkingZoneByKeywordDTO;
import org.example.honorsparkingbe.dto.ParkingFeeRuleDTO;
import org.example.honorsparkingbe.dto.ParkingZoneWithMatchedInfoDTO;
import org.example.honorsparkingbe.dto.ParkingZoneWithMatchedInfoDTO.MatchedInfoElement;
import org.example.honorsparkingbe.dto.response.PaginationResponse;
import org.example.honorsparkingbe.dto.response.ParkingZoneSearchResponse;
import org.example.honorsparkingbe.dto.response.ParkingZoneSearchResponse.Meta;
import org.example.honorsparkingbe.repository.internal.FavoriteParkingZoneRepository;
import org.example.honorsparkingbe.repository.internal.ParkingZoneRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingZoneSearchService {

  private final ParkingZoneRepository parkingZoneRepository;
  private final FavoriteParkingZoneRepository favoriteParkingZoneRepository;

  public ParkingZoneSearchResponse getParkingZonesByKeyword(GetParkingZoneByKeywordDTO dto) {
    String keyword = dto.getKeyword();
    int pageSize = 10;
    int currentPage = dto.getPage() == null ? 0 : dto.getPage();
    Pageable pageable = PageRequest.of(
        currentPage,
        pageSize);

    List<ParkingZoneEntity> zones = parkingZoneRepository.searchByKeyword(keyword, pageable)
        .stream().toList();
    // 1. Favorite parkingZone id가져와서 set에 저장
    Set<Long> favoriteParkingZoneIds = favoriteParkingZoneRepository
        .findAllByMemberEntity_Id(dto.getMemberId()).stream().map(
            fz -> fz.getParkingZoneEntity().getId()).collect(Collectors.toSet());

    // 2. ParkingZoneWithMatchedInfoDTO로 변환
    List<ParkingZoneWithMatchedInfoDTO> parkingZoneResult = zones.stream()
        .map(zone -> {
          List<MatchedInfoElement> matchedInfos = new ArrayList<>();

          Map<String, String> fieldToValueMap = new LinkedHashMap<>();
          fieldToValueMap.put("zoneName", zone.getZoneName());
          fieldToValueMap.put("cityName", zone.getCityEntity().getName());
          fieldToValueMap.put("districtName", zone.getDistrictEntity().getName());
          fieldToValueMap.put("eupMyeonDongName", zone.getEupMyeonDongEntity().getName());
          fieldToValueMap.put("address", zone.getCityEntity().getName() + " " +
              zone.getDistrictEntity().getName() + " " +
              zone.getEupMyeonDongEntity().getName());

          for (Map.Entry<String, String> entry : fieldToValueMap.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();

            int startIndex = value.indexOf(keyword);
            if (startIndex >= 0) {
              matchedInfos.add(MatchedInfoElement.builder()
                  .field(field)
                  .value(value)
                  .matchedText(keyword)
                  .startIndex(startIndex)
                  .endIndex(startIndex + keyword.length())
                  .build());
            }
          }

          return ParkingZoneWithMatchedInfoDTO.builder()
              .matchedInfo(matchedInfos)
              .isFavorite(
                  favoriteParkingZoneIds.contains(zone.getId()) ? Boolean.TRUE : Boolean.FALSE)
              .zoneName(fieldToValueMap.get("zoneName"))
              .cityName(fieldToValueMap.get("cityName"))
              .districtName(fieldToValueMap.get("districtName"))
              .eupMyeonDongName(fieldToValueMap.get("eupMyeonDongName"))
              .address(fieldToValueMap.get("address"))
              .electricCarSpaceCount(zone.getElectricCarSpaceCount())
              .latitude(zone.getLatitude())
              .longitude(zone.getLongitude())
              .size(zone.getSize())
              .maxCost(zone.getMaxCost())
              .parkingFeeRules(
                  zone.getParkingFeeRuleEntities().stream()
                      .map(rule -> ParkingFeeRuleDTO.builder()
                          .ruleName(rule.getRuleName())
                          .startTime(rule.getStartTime())
                          .endTime(rule.getEndTime())
                          .costPerTimeSlot(rule.getCostPerTimeSlot())
                          .costTimeSlot(rule.getCostTimeSlot())
                          .build()
                      )
                      .collect(Collectors.toList())
              )
              .thumbnail(zone.getThumbnailUrl())
              .build();

        }).toList();
    // 3. page DTO생성
    Long totalDatas = parkingZoneRepository.countByKeyword(keyword);
    long totalPages = totalDatas == 0 ? 0 : totalDatas / 10 + 1;
    boolean isEnd = totalPages == currentPage + 1;
    isEnd = totalPages == 0 || isEnd;
    PaginationResponse paginationResponse = PaginationResponse.builder()
        .currentPage(currentPage)
        .totalPages(totalDatas == 0 ? 0 : totalDatas / 10 + 1)
        .pageSize(pageSize)
        .totalItems(totalDatas)
        .build();

    return ParkingZoneSearchResponse.builder()
        .meta(Meta.builder()
            .isEnd(isEnd)
            .keyword(keyword)
            .pagination(paginationResponse)
            .build())
        .parkingZones(parkingZoneResult)
        .build();
  }
}
