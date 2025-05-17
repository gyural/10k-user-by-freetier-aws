package org.example.honorsparkingbe.unit.service;

import static org.example.honorsparkingbe.domain.enums.LoginPlatform.NORMAL;
import static org.example.honorsparkingbe.domain.enums.MemberRole.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.CityEntity;
import org.example.honorsparkingbe.domain.entity.DistrictEntity;
import org.example.honorsparkingbe.domain.entity.EupMyeonDongEntity;
import org.example.honorsparkingbe.domain.entity.FavoriteParkingZoneEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.dto.GetParkingZoneByKeywordDTO;
import org.example.honorsparkingbe.dto.ParkingZoneWithMatchedInfoDTO;
import org.example.honorsparkingbe.dto.ParkingZoneWithMatchedInfoDTO.MatchedInfoElement;
import org.example.honorsparkingbe.dto.response.ParkingZoneSearchResponse;
import org.example.honorsparkingbe.repository.internal.FavoriteParkingZoneRepository;
import org.example.honorsparkingbe.repository.internal.ParkingZoneRepository;
import org.example.honorsparkingbe.service.ParkingZoneSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
public class ParkingZoneSearchServiceTest {

  @InjectMocks
  ParkingZoneSearchService parkingZoneSearchService;

  @Mock
  ParkingZoneRepository parkingZoneRepository;
  @Mock
  FavoriteParkingZoneRepository favoriteParkingZoneRepository;

  @Test
  @DisplayName("키워드와 일치하는 주차장이 없을 때 반환 확인")
  void keywordNotMatchedTest() {
    // Given
    String keyword = "nonMatchedKeyword";
    Long memberId = 999L;
    GetParkingZoneByKeywordDTO dto = GetParkingZoneByKeywordDTO.builder()
        .keyword(keyword)
        .memberId(memberId)
        .page(0)
        .build();

    when(parkingZoneRepository.searchByKeyword(
        eq(keyword), eq(PageRequest.of(0, 10)))).thenReturn(Page.empty());

    when(parkingZoneRepository.countByKeyword(eq(keyword))).thenReturn(0L);
    when(favoriteParkingZoneRepository.findAllByMemberEntity_Id(eq(memberId))).thenReturn(Set.of());

    // When
    ParkingZoneSearchResponse res = parkingZoneSearchService.getParkingZonesByKeyword(dto);

    // Then
    verify(parkingZoneRepository, times(1)).searchByKeyword(
        eq(keyword), eq(PageRequest.of(0, 10)));
    verify(parkingZoneRepository, times(1)).countByKeyword(eq(keyword));
    verify(favoriteParkingZoneRepository, times(1))
        .findAllByMemberEntity_Id(eq(memberId));

    assertEquals(res.getMeta().getKeyword(), keyword);
    assertEquals(res.getMeta().getIsEnd(), true);
    assertEquals(res.getParkingZones().size(), 0);
    assertEquals(res.getMeta().getPagination().getCurrentPage(), 0);
    assertEquals(res.getMeta().getPagination().getPageSize(), 10);
    assertEquals(res.getMeta().getPagination().getTotalItems(), 0);
    assertEquals(res.getMeta().getPagination().getTotalPages(), 0);

  }

  @Test
  @DisplayName("주차장 이름만 키워드와 일치할때 반환 확인")
  void keywordMatchedOnlyZoneNameTest() {
    // Given
    String keyword = "서울";
    Long memberId = 999L;
    GetParkingZoneByKeywordDTO dto = GetParkingZoneByKeywordDTO.builder()
        .keyword(keyword)
        .memberId(memberId)
        .page(0)
        .build();

    CityEntity mockCityEntity = new CityEntity(1L, "조치원시");
    DistrictEntity mockDistrictEntity = new DistrictEntity(1L, "중구");
    EupMyeonDongEntity mockEupMyeonDongEntity = new EupMyeonDongEntity(1L, "중구동");
    String mockAddress = mockCityEntity.getName() + " "
        + mockDistrictEntity.getName() + " "
        + mockEupMyeonDongEntity.getName();

    List<ParkingZoneEntity> mockZones = List.of(
        ParkingZoneEntity.builder()
            .id(1L).zoneName("서울역 주차장").latitude(36.5).longitude(36.5)
            .cityEntity(mockCityEntity).districtEntity(mockDistrictEntity)
            .eupMyeonDongEntity(mockEupMyeonDongEntity)
            .electricCarSpaceCount(0).size(100).maxCost(0)
            .address("testAddress")
            .parkingFeeRuleEntities(List.of())
            .thumbnailUrl("www.test.com")
            .build(),
        ParkingZoneEntity.builder()
            .id(2L).zoneName("시청서울 지하주차장").latitude(36.5).longitude(36.5)
            .cityEntity(mockCityEntity).districtEntity(mockDistrictEntity)
            .eupMyeonDongEntity(mockEupMyeonDongEntity)
            .electricCarSpaceCount(0)
            .size(100).maxCost(0).address("testAddress")
            .parkingFeeRuleEntities(List.of())
            .thumbnailUrl("www.test.com")
            .build()
    );

    ParkingZoneWithMatchedInfoDTO expectParkingZoneData1 = ParkingZoneWithMatchedInfoDTO
        .builder()
        .matchedInfo(
            List.of(MatchedInfoElement
                .builder()
                .field("zoneName")
                .value(mockZones.get(0).getZoneName())
                .matchedText("서울")
                .startIndex(0)
                .endIndex(2)
                .build()
            ))
        .isFavorite(false)
        .address(mockAddress)
        .latitude(mockZones.get(0).getLatitude())
        .longitude(mockZones.get(0).getLongitude())
        .zoneName(mockZones.get(0).getZoneName())
        .cityName(mockZones.get(0).getCityEntity().getName())
        .districtName(mockZones.get(0).getDistrictEntity().getName())
        .eupMyeonDongName(mockZones.get(0).getEupMyeonDongEntity().getName())
        .electricCarSpaceCount(mockZones.get(0).getElectricCarSpaceCount())
        .size(mockZones.get(0).getSize())
        .maxCost(mockZones.get(0).getMaxCost())
        .parkingFeeRules(List.of())
        .thumbnail(mockZones.get(0).getThumbnailUrl())
        .build();

    ParkingZoneWithMatchedInfoDTO expectParkingZoneData2 = ParkingZoneWithMatchedInfoDTO
        .builder()
        .matchedInfo(List.of(MatchedInfoElement
            .builder()
            .field("zoneName")
            .value(mockZones.get(1).getZoneName())
            .matchedText("서울")
            .startIndex(2)
            .endIndex(4)
            .build()
        ))
        .isFavorite(false)
        .address(mockAddress)
        .latitude(mockZones.get(1).getLatitude())
        .longitude(mockZones.get(1).getLongitude())
        .zoneName(mockZones.get(1).getZoneName())
        .cityName(mockZones.get(1).getCityEntity().getName())
        .districtName(mockZones.get(1).getDistrictEntity().getName())
        .eupMyeonDongName(mockZones.get(1).getEupMyeonDongEntity().getName())
        .electricCarSpaceCount(mockZones.get(1).getElectricCarSpaceCount())
        .size(mockZones.get(1).getSize())
        .maxCost(mockZones.get(1).getMaxCost())
        .parkingFeeRules(List.of())
        .thumbnail(mockZones.get(1).getThumbnailUrl())
        .build();

    Page<ParkingZoneEntity> mockPage = new PageImpl<>(
        mockZones,
        PageRequest.of(0, 10),
        mockZones.size());

    when(parkingZoneRepository.searchByKeyword(
        eq(keyword), eq(PageRequest.of(0, 10)))).thenReturn(mockPage);
    when(parkingZoneRepository.countByKeyword(eq(keyword))).thenReturn(2L);
    when(favoriteParkingZoneRepository.findAllByMemberEntity_Id(eq(memberId))).thenReturn(Set.of());

    // When
    ParkingZoneSearchResponse res = parkingZoneSearchService.getParkingZonesByKeyword(dto);

    // Then
    verify(parkingZoneRepository, times(1)).searchByKeyword(
        eq(keyword), eq(PageRequest.of(0, 10)));
    verify(parkingZoneRepository, times(1)).countByKeyword(eq(keyword));
    verify(favoriteParkingZoneRepository, times(1))
        .findAllByMemberEntity_Id(eq(memberId));

    assertEquals(res.getMeta().getKeyword(), keyword);
    assertEquals(res.getMeta().getIsEnd(), true);

    assertEquals(res.getMeta().getPagination().getCurrentPage(), 0);
    assertEquals(res.getMeta().getPagination().getTotalPages(), 1);
    assertEquals(res.getMeta().getPagination().getPageSize(), 10);
    assertEquals(res.getMeta().getPagination().getTotalItems(), 2);

    assertEquals(2, res.getParkingZones().size());
    assertEquals(expectParkingZoneData1, res.getParkingZones().get(0));
    assertEquals(expectParkingZoneData2, res.getParkingZones().get(1));

  }

  @Test
  @DisplayName("주차장 이름 제외 나머지 하나(City)만 일치할 때 반환 확인")
  void keywordMatchedOnlyCityNameTest() {
    // Given
    String keyword = "서울";
    Long memberId = 999L;
    GetParkingZoneByKeywordDTO dto = GetParkingZoneByKeywordDTO.builder()
        .keyword(keyword)
        .memberId(memberId)
        .page(0)
        .build();

    CityEntity mockCityEntity = new CityEntity(1L, "서울시");
    DistrictEntity mockDistrictEntity = new DistrictEntity(1L, "중구");
    EupMyeonDongEntity mockEupMyeonDongEntity = new EupMyeonDongEntity(1L, "중구동");
    String mockAddress = mockCityEntity.getName() + " "
        + mockDistrictEntity.getName() + " "
        + mockEupMyeonDongEntity.getName();

    List<ParkingZoneEntity> mockZones = List.of(
        ParkingZoneEntity.builder()
            .id(1L).zoneName("조치원 주차장").latitude(36.5).longitude(36.5)
            .cityEntity(mockCityEntity).districtEntity(mockDistrictEntity)
            .eupMyeonDongEntity(mockEupMyeonDongEntity)
            .electricCarSpaceCount(0).size(100).maxCost(0)
            .address("testAddress")
            .parkingFeeRuleEntities(List.of())
            .thumbnailUrl("www.test.com")
            .build(),
        ParkingZoneEntity.builder()
            .id(2L).zoneName("조치원 지하주차장").latitude(36.5).longitude(36.5)
            .cityEntity(mockCityEntity).districtEntity(mockDistrictEntity)
            .eupMyeonDongEntity(mockEupMyeonDongEntity)
            .electricCarSpaceCount(0)
            .size(100).maxCost(0).address("testAddress")
            .parkingFeeRuleEntities(List.of())
            .thumbnailUrl("www.test.com")
            .build()
    );

    ParkingZoneWithMatchedInfoDTO expectParkingZoneData1 = ParkingZoneWithMatchedInfoDTO
        .builder()
        .matchedInfo(List.of(
                MatchedInfoElement
                    .builder()
                    .field("cityName")
                    .value(mockZones.get(1).getCityEntity().getName())
                    .matchedText("서울")
                    .startIndex(0)
                    .endIndex(2)
                    .build(),
                MatchedInfoElement
                    .builder()
                    .field("address")
                    .value(mockAddress)
                    .matchedText("서울")
                    .startIndex(0)
                    .endIndex(2)
                    .build()
            )
        )
        .isFavorite(false)
        .latitude(mockZones.get(0).getLatitude())
        .longitude(mockZones.get(0).getLongitude())
        .zoneName(mockZones.get(0).getZoneName())
        .cityName(mockZones.get(0).getCityEntity().getName())
        .districtName(mockZones.get(0).getDistrictEntity().getName())
        .eupMyeonDongName(mockZones.get(0).getEupMyeonDongEntity().getName())
        .address(mockAddress)
        .electricCarSpaceCount(mockZones.get(0).getElectricCarSpaceCount())
        .size(mockZones.get(0).getSize())
        .maxCost(mockZones.get(0).getMaxCost())
        .parkingFeeRules(List.of())
        .thumbnail(mockZones.get(0).getThumbnailUrl())
        .build();

    ParkingZoneWithMatchedInfoDTO expectParkingZoneData2 = ParkingZoneWithMatchedInfoDTO
        .builder()
        .matchedInfo(List.of(
                MatchedInfoElement
                    .builder()
                    .field("cityName")
                    .value(mockZones.get(1).getCityEntity().getName())
                    .matchedText("서울")
                    .startIndex(0)
                    .endIndex(2)
                    .build(),
                MatchedInfoElement
                    .builder()
                    .field("address")
                    .value(mockAddress)
                    .matchedText("서울")
                    .startIndex(0)
                    .endIndex(2)
                    .build()
            )
        )
        .isFavorite(false)
        .latitude(mockZones.get(1).getLatitude())
        .longitude(mockZones.get(1).getLongitude())
        .zoneName(mockZones.get(1).getZoneName())
        .cityName(mockZones.get(1).getCityEntity().getName())
        .districtName(mockZones.get(1).getDistrictEntity().getName())
        .eupMyeonDongName(mockZones.get(1).getEupMyeonDongEntity().getName())
        .address(mockZones.get(0).getCityEntity().getName() + " "
            + mockZones.get(0).getDistrictEntity().getName() + " "
            + mockZones.get(0).getEupMyeonDongEntity().getName()
        )
        .electricCarSpaceCount(mockZones.get(1).getElectricCarSpaceCount())
        .size(mockZones.get(1).getSize())
        .maxCost(mockZones.get(1).getMaxCost())
        .parkingFeeRules(List.of())
        .thumbnail(mockZones.get(1).getThumbnailUrl())
        .build();

    Page<ParkingZoneEntity> mockPage = new PageImpl<>(
        mockZones,
        PageRequest.of(0, 10),
        mockZones.size());

    when(parkingZoneRepository.searchByKeyword(
        eq(keyword), eq(PageRequest.of(0, 10)))).thenReturn(mockPage);
    when(parkingZoneRepository.countByKeyword(eq(keyword))).thenReturn(2L);
    when(favoriteParkingZoneRepository.findAllByMemberEntity_Id(eq(memberId))).thenReturn(Set.of());

    // When
    ParkingZoneSearchResponse res = parkingZoneSearchService.getParkingZonesByKeyword(dto);

    // Then
    verify(parkingZoneRepository, times(1)).searchByKeyword(
        eq(keyword), eq(PageRequest.of(0, 10)));
    verify(parkingZoneRepository, times(1)).countByKeyword(eq(keyword));
    verify(favoriteParkingZoneRepository, times(1))
        .findAllByMemberEntity_Id(eq(memberId));

    assertEquals(res.getMeta().getKeyword(), keyword);
    assertEquals(res.getMeta().getIsEnd(), true);

    assertEquals(res.getMeta().getPagination().getCurrentPage(), 0);
    assertEquals(res.getMeta().getPagination().getTotalPages(), 1);
    assertEquals(res.getMeta().getPagination().getPageSize(), 10);
    assertEquals(res.getMeta().getPagination().getTotalItems(), 2);

    assertEquals(2, res.getParkingZones().size());
    assertEquals(expectParkingZoneData1, res.getParkingZones().get(0));
    assertEquals(expectParkingZoneData2, res.getParkingZones().get(1));

  }

  @Test
  @DisplayName("모든 필드(주차장 이름, 주소)가 일치할 때 반환 확인")
  void keywordNotMatchedAllFieldTest() {
    // Given
    String keyword = "서울시 중구 중구동";
    Long memberId = 999L;
    GetParkingZoneByKeywordDTO dto = GetParkingZoneByKeywordDTO.builder()
        .keyword(keyword)
        .memberId(memberId)
        .page(0)
        .build();

    CityEntity mockCityEntity = new CityEntity(1L, "서울시");
    DistrictEntity mockDistrictEntity = new DistrictEntity(1L, "중구");
    EupMyeonDongEntity mockEupMyeonDongEntity = new EupMyeonDongEntity(1L, "중구동");
    String mockAddress = mockCityEntity.getName() + " "
        + mockDistrictEntity.getName() + " "
        + mockEupMyeonDongEntity.getName();

    List<ParkingZoneEntity> mockZones = List.of(
        ParkingZoneEntity.builder()
            .id(1L).zoneName("서울시 중구 중구동 1주차장").latitude(36.5).longitude(36.5)
            .cityEntity(mockCityEntity).districtEntity(mockDistrictEntity)
            .eupMyeonDongEntity(mockEupMyeonDongEntity)
            .electricCarSpaceCount(0).size(100).maxCost(0)
            .address("testAddress")
            .parkingFeeRuleEntities(List.of())
            .thumbnailUrl("www.test.com")
            .build(),
        ParkingZoneEntity.builder()
            .id(2L).zoneName("서울시 중구 중구동 2주차장").latitude(36.5).longitude(36.5)
            .cityEntity(mockCityEntity).districtEntity(mockDistrictEntity)
            .eupMyeonDongEntity(mockEupMyeonDongEntity)
            .electricCarSpaceCount(0)
            .size(100).maxCost(0).address("testAddress")
            .parkingFeeRuleEntities(List.of())
            .thumbnailUrl("www.test.com")
            .build()
    );

    ParkingZoneWithMatchedInfoDTO expectParkingZoneData1 = ParkingZoneWithMatchedInfoDTO
        .builder()
        .matchedInfo(List.of(
                MatchedInfoElement
                    .builder()
                    .field("zoneName")
                    .value(mockZones.get(0).getZoneName())
                    .matchedText(keyword)
                    .startIndex(0)
                    .endIndex(10)
                    .build(),
                MatchedInfoElement
                    .builder()
                    .field("address")
                    .value(mockAddress)
                    .matchedText(keyword)
                    .startIndex(0)
                    .endIndex(10)
                    .build()
            )
        )
        .isFavorite(false)
        .latitude(mockZones.get(0).getLatitude())
        .longitude(mockZones.get(0).getLongitude())
        .zoneName(mockZones.get(0).getZoneName())
        .cityName(mockZones.get(0).getCityEntity().getName())
        .districtName(mockZones.get(0).getDistrictEntity().getName())
        .eupMyeonDongName(mockZones.get(0).getEupMyeonDongEntity().getName())
        .address(mockAddress)
        .electricCarSpaceCount(mockZones.get(0).getElectricCarSpaceCount())
        .size(mockZones.get(0).getSize())
        .maxCost(mockZones.get(0).getMaxCost())
        .parkingFeeRules(List.of())
        .thumbnail(mockZones.get(0).getThumbnailUrl())
        .build();

    ParkingZoneWithMatchedInfoDTO expectParkingZoneData2 = ParkingZoneWithMatchedInfoDTO
        .builder()
        .matchedInfo(List.of(
                MatchedInfoElement
                    .builder()
                    .field("zoneName")
                    .value(mockZones.get(1).getZoneName())
                    .matchedText(keyword)
                    .startIndex(0)
                    .endIndex(10)
                    .build(),
                MatchedInfoElement
                    .builder()
                    .field("address")
                    .value(mockAddress)
                    .matchedText(keyword)
                    .startIndex(0)
                    .endIndex(10)
                    .build()
            )
        )
        .isFavorite(false)
        .latitude(mockZones.get(1).getLatitude())
        .longitude(mockZones.get(1).getLongitude())
        .zoneName(mockZones.get(1).getZoneName())
        .cityName(mockZones.get(1).getCityEntity().getName())
        .districtName(mockZones.get(1).getDistrictEntity().getName())
        .eupMyeonDongName(mockZones.get(1).getEupMyeonDongEntity().getName())
        .address(mockZones.get(0).getCityEntity().getName() + " "
            + mockZones.get(0).getDistrictEntity().getName() + " "
            + mockZones.get(0).getEupMyeonDongEntity().getName()
        )
        .electricCarSpaceCount(mockZones.get(1).getElectricCarSpaceCount())
        .size(mockZones.get(1).getSize())
        .maxCost(mockZones.get(1).getMaxCost())
        .parkingFeeRules(List.of())
        .thumbnail(mockZones.get(1).getThumbnailUrl())
        .build();

    Page<ParkingZoneEntity> mockPage = new PageImpl<>(
        mockZones,
        PageRequest.of(0, 10),
        mockZones.size());

    when(parkingZoneRepository.searchByKeyword(
        eq(keyword), eq(PageRequest.of(0, 10)))).thenReturn(mockPage);
    when(parkingZoneRepository.countByKeyword(eq(keyword))).thenReturn(2L);
    when(favoriteParkingZoneRepository.findAllByMemberEntity_Id(eq(memberId))).thenReturn(Set.of());

    // When
    ParkingZoneSearchResponse res = parkingZoneSearchService.getParkingZonesByKeyword(dto);

    // Then
    verify(parkingZoneRepository, times(1)).searchByKeyword(
        eq(keyword), eq(PageRequest.of(0, 10)));
    verify(parkingZoneRepository, times(1)).countByKeyword(eq(keyword));
    verify(favoriteParkingZoneRepository, times(1))
        .findAllByMemberEntity_Id(eq(memberId));

    assertEquals(res.getMeta().getKeyword(), keyword);
    assertEquals(res.getMeta().getIsEnd(), true);

    assertEquals(res.getMeta().getPagination().getCurrentPage(), 0);
    assertEquals(res.getMeta().getPagination().getTotalPages(), 1);
    assertEquals(res.getMeta().getPagination().getPageSize(), 10);
    assertEquals(res.getMeta().getPagination().getTotalItems(), 2);

    assertEquals(2, res.getParkingZones().size());
    assertEquals(expectParkingZoneData1, res.getParkingZones().get(0));
    assertEquals(expectParkingZoneData2, res.getParkingZones().get(1));

  }

  @Test
  @DisplayName("키워드 매칭된 주차장이 pageSize가 넘어가고 1이상 페이지 요청 시 반환 확인")
  void keywordMatchedPageSizeTest() {
    // Given
    String keyword = "서울";
    Long memberId = 999L;
    GetParkingZoneByKeywordDTO dto = GetParkingZoneByKeywordDTO.builder()
        .keyword(keyword)
        .memberId(memberId)
        .page(1)
        .build();

    CityEntity mockCityEntity = new CityEntity(1L, "서울시");
    DistrictEntity mockDistrictEntity = new DistrictEntity(1L, "중구");
    EupMyeonDongEntity mockEupMyeonDongEntity = new EupMyeonDongEntity(1L, "중구동");
    String mockAddress = mockCityEntity.getName() + " "
        + mockDistrictEntity.getName() + " "
        + mockEupMyeonDongEntity.getName();

    List<ParkingZoneEntity> mockZones = List.of(
        ParkingZoneEntity.builder()
            .id(11L).zoneName("조치원 주차장").latitude(36.5).longitude(36.5)
            .cityEntity(mockCityEntity).districtEntity(mockDistrictEntity)
            .eupMyeonDongEntity(mockEupMyeonDongEntity)
            .electricCarSpaceCount(0).size(100).maxCost(0)
            .address("testAddress")
            .parkingFeeRuleEntities(List.of())
            .thumbnailUrl("www.test.com")
            .build(),
        ParkingZoneEntity.builder()
            .id(12L).zoneName("조치원 지하주차장").latitude(36.5).longitude(36.5)
            .cityEntity(mockCityEntity).districtEntity(mockDistrictEntity)
            .eupMyeonDongEntity(mockEupMyeonDongEntity)
            .electricCarSpaceCount(0)
            .size(100).maxCost(0).address("testAddress")
            .parkingFeeRuleEntities(List.of())
            .thumbnailUrl("www.test.com")
            .build()
    );

    ParkingZoneWithMatchedInfoDTO expectParkingZoneData1 = ParkingZoneWithMatchedInfoDTO
        .builder()
        .matchedInfo(List.of(
                MatchedInfoElement
                    .builder()
                    .field("cityName")
                    .value(mockZones.get(1).getCityEntity().getName())
                    .matchedText("서울")
                    .startIndex(0)
                    .endIndex(2)
                    .build(),
                MatchedInfoElement
                    .builder()
                    .field("address")
                    .value(mockAddress)
                    .matchedText("서울")
                    .startIndex(0)
                    .endIndex(2)
                    .build()
            )
        )
        .isFavorite(false)
        .latitude(mockZones.get(0).getLatitude())
        .longitude(mockZones.get(0).getLongitude())
        .zoneName(mockZones.get(0).getZoneName())
        .cityName(mockZones.get(0).getCityEntity().getName())
        .districtName(mockZones.get(0).getDistrictEntity().getName())
        .eupMyeonDongName(mockZones.get(0).getEupMyeonDongEntity().getName())
        .address(mockAddress)
        .electricCarSpaceCount(mockZones.get(0).getElectricCarSpaceCount())
        .size(mockZones.get(0).getSize())
        .maxCost(mockZones.get(0).getMaxCost())
        .parkingFeeRules(List.of())
        .thumbnail(mockZones.get(0).getThumbnailUrl())
        .build();

    ParkingZoneWithMatchedInfoDTO expectParkingZoneData2 = ParkingZoneWithMatchedInfoDTO
        .builder()
        .matchedInfo(List.of(
                MatchedInfoElement
                    .builder()
                    .field("cityName")
                    .value(mockZones.get(1).getCityEntity().getName())
                    .matchedText("서울")
                    .startIndex(0)
                    .endIndex(2)
                    .build(),
                MatchedInfoElement
                    .builder()
                    .field("address")
                    .value(mockAddress)
                    .matchedText("서울")
                    .startIndex(0)
                    .endIndex(2)
                    .build()
            )
        )
        .isFavorite(false)
        .latitude(mockZones.get(1).getLatitude())
        .longitude(mockZones.get(1).getLongitude())
        .zoneName(mockZones.get(1).getZoneName())
        .cityName(mockZones.get(1).getCityEntity().getName())
        .districtName(mockZones.get(1).getDistrictEntity().getName())
        .eupMyeonDongName(mockZones.get(1).getEupMyeonDongEntity().getName())
        .address(mockZones.get(0).getCityEntity().getName() + " "
            + mockZones.get(0).getDistrictEntity().getName() + " "
            + mockZones.get(0).getEupMyeonDongEntity().getName()
        )
        .electricCarSpaceCount(mockZones.get(1).getElectricCarSpaceCount())
        .size(mockZones.get(1).getSize())
        .maxCost(mockZones.get(1).getMaxCost())
        .parkingFeeRules(List.of())
        .thumbnail(mockZones.get(1).getThumbnailUrl())
        .build();

    Page<ParkingZoneEntity> mockPage = new PageImpl<>(
        mockZones,
        PageRequest.of(1, 10),
        mockZones.size());

    when(parkingZoneRepository.searchByKeyword(
        eq(keyword), eq(PageRequest.of(1, 10)))).thenReturn(mockPage);
    when(parkingZoneRepository.countByKeyword(eq(keyword))).thenReturn(12L);
    when(favoriteParkingZoneRepository.findAllByMemberEntity_Id(eq(memberId))).thenReturn(Set.of());

    // When
    ParkingZoneSearchResponse res = parkingZoneSearchService.getParkingZonesByKeyword(dto);

    // Then
    verify(parkingZoneRepository, times(1)).searchByKeyword(
        eq(keyword), eq(PageRequest.of(1, 10)));
    verify(parkingZoneRepository, times(1)).countByKeyword(eq(keyword));
    verify(favoriteParkingZoneRepository, times(1))
        .findAllByMemberEntity_Id(eq(memberId));

    assertEquals(res.getMeta().getKeyword(), keyword);
    assertEquals(res.getMeta().getIsEnd(), true);

    assertEquals(res.getMeta().getPagination().getCurrentPage(), 1);
    assertEquals(res.getMeta().getPagination().getTotalPages(), 2);
    assertEquals(res.getMeta().getPagination().getPageSize(), 10);
    assertEquals(res.getMeta().getPagination().getTotalItems(), 12);

    assertEquals(2, res.getParkingZones().size());
    assertEquals(expectParkingZoneData1, res.getParkingZones().get(0));
    assertEquals(expectParkingZoneData2, res.getParkingZones().get(1));

  }

  @Test
  @DisplayName("키워드 매칭된 주차장의 즐겨찾기가 잘 반영되는지 확인")
  void keywordNotMatchedBookMarkTest() {
    // Given
    // 1L은 Non Favorite 2L은 Favorite
    String keyword = "서울";
    Long memberId = 999L;
    CarEntity mockCar = CarEntity.builder()
        .id(1L)
        .carNumber("123가 1234")
        .createdAt(LocalDateTime.now())
        .build();
    MemberEntity mockMemberEntity = MemberEntity.builder()
        .id(memberId)
        .carEntity(mockCar)
        .loginPlatform(NORMAL)
        .role(ROLE_USER)
        .userName("mockUser")
        .authId("mockId")
        .build();
    GetParkingZoneByKeywordDTO dto = GetParkingZoneByKeywordDTO.builder()
        .keyword(keyword)
        .memberId(memberId)
        .page(0)
        .build();

    CityEntity mockCityEntity = new CityEntity(1L, "서울시");
    DistrictEntity mockDistrictEntity = new DistrictEntity(1L, "중구");
    EupMyeonDongEntity mockEupMyeonDongEntity = new EupMyeonDongEntity(1L, "중구동");
    String mockAddress = mockCityEntity.getName() + " "
        + mockDistrictEntity.getName() + " "
        + mockEupMyeonDongEntity.getName();

    List<ParkingZoneEntity> mockZones = List.of(
        ParkingZoneEntity.builder()
            .id(1L).zoneName("조치원 주차장").latitude(36.5).longitude(36.5)
            .cityEntity(mockCityEntity).districtEntity(mockDistrictEntity)
            .eupMyeonDongEntity(mockEupMyeonDongEntity)
            .electricCarSpaceCount(0).size(100).maxCost(0)
            .address("testAddress")
            .parkingFeeRuleEntities(List.of())
            .thumbnailUrl("www.test.com")
            .build(),
        ParkingZoneEntity.builder()
            .id(2L).zoneName("조치원 지하주차장").latitude(36.5).longitude(36.5)
            .cityEntity(mockCityEntity).districtEntity(mockDistrictEntity)
            .eupMyeonDongEntity(mockEupMyeonDongEntity)
            .electricCarSpaceCount(0)
            .size(100).maxCost(0).address("testAddress")
            .parkingFeeRuleEntities(List.of())
            .thumbnailUrl("www.test.com")
            .build()
    );

    ParkingZoneWithMatchedInfoDTO expectParkingZoneData1 = ParkingZoneWithMatchedInfoDTO
        .builder()
        .matchedInfo(List.of(
                MatchedInfoElement
                    .builder()
                    .field("cityName")
                    .value(mockZones.get(1).getCityEntity().getName())
                    .matchedText("서울")
                    .startIndex(0)
                    .endIndex(2)
                    .build(),
                MatchedInfoElement
                    .builder()
                    .field("address")
                    .value(mockAddress)
                    .matchedText("서울")
                    .startIndex(0)
                    .endIndex(2)
                    .build()
            )
        )
        .isFavorite(false)
        .latitude(mockZones.get(0).getLatitude())
        .longitude(mockZones.get(0).getLongitude())
        .zoneName(mockZones.get(0).getZoneName())
        .cityName(mockZones.get(0).getCityEntity().getName())
        .districtName(mockZones.get(0).getDistrictEntity().getName())
        .eupMyeonDongName(mockZones.get(0).getEupMyeonDongEntity().getName())
        .address(mockAddress)
        .electricCarSpaceCount(mockZones.get(0).getElectricCarSpaceCount())
        .size(mockZones.get(0).getSize())
        .maxCost(mockZones.get(0).getMaxCost())
        .parkingFeeRules(List.of())
        .thumbnail(mockZones.get(0).getThumbnailUrl())
        .build();

    ParkingZoneWithMatchedInfoDTO expectParkingZoneData2 = ParkingZoneWithMatchedInfoDTO
        .builder()
        .matchedInfo(List.of(
                MatchedInfoElement
                    .builder()
                    .field("cityName")
                    .value(mockZones.get(1).getCityEntity().getName())
                    .matchedText("서울")
                    .startIndex(0)
                    .endIndex(2)
                    .build(),
                MatchedInfoElement
                    .builder()
                    .field("address")
                    .value(mockAddress)
                    .matchedText("서울")
                    .startIndex(0)
                    .endIndex(2)
                    .build()
            )
        )
        .isFavorite(true)
        .latitude(mockZones.get(1).getLatitude())
        .longitude(mockZones.get(1).getLongitude())
        .zoneName(mockZones.get(1).getZoneName())
        .cityName(mockZones.get(1).getCityEntity().getName())
        .districtName(mockZones.get(1).getDistrictEntity().getName())
        .eupMyeonDongName(mockZones.get(1).getEupMyeonDongEntity().getName())
        .address(mockZones.get(0).getCityEntity().getName() + " "
            + mockZones.get(0).getDistrictEntity().getName() + " "
            + mockZones.get(0).getEupMyeonDongEntity().getName()
        )
        .electricCarSpaceCount(mockZones.get(1).getElectricCarSpaceCount())
        .size(mockZones.get(1).getSize())
        .maxCost(mockZones.get(1).getMaxCost())
        .parkingFeeRules(List.of())
        .thumbnail(mockZones.get(1).getThumbnailUrl())
        .build();

    Page<ParkingZoneEntity> mockPage = new PageImpl<>(
        mockZones,
        PageRequest.of(0, 10),
        mockZones.size());

    when(parkingZoneRepository.searchByKeyword(
        eq(keyword), eq(PageRequest.of(0, 10)))).thenReturn(mockPage);
    when(parkingZoneRepository.countByKeyword(eq(keyword))).thenReturn(2L);
    when(favoriteParkingZoneRepository.findAllByMemberEntity_Id(eq(memberId))).thenReturn(Set.of(
        FavoriteParkingZoneEntity.builder()
            .memberEntity(mockMemberEntity)
            .parkingZoneEntity(mockZones.get(1))
            .build()));

    // When
    ParkingZoneSearchResponse res = parkingZoneSearchService.getParkingZonesByKeyword(dto);

    // Then
    verify(parkingZoneRepository, times(1)).searchByKeyword(
        eq(keyword), eq(PageRequest.of(0, 10)));
    verify(parkingZoneRepository, times(1)).countByKeyword(eq(keyword));
    verify(favoriteParkingZoneRepository, times(1))
        .findAllByMemberEntity_Id(eq(memberId));

    assertEquals(res.getMeta().getKeyword(), keyword);
    assertEquals(res.getMeta().getIsEnd(), true);

    assertEquals(res.getMeta().getPagination().getCurrentPage(), 0);
    assertEquals(res.getMeta().getPagination().getTotalPages(), 1);
    assertEquals(res.getMeta().getPagination().getPageSize(), 10);
    assertEquals(res.getMeta().getPagination().getTotalItems(), 2);

    assertEquals(2, res.getParkingZones().size());
    assertEquals(expectParkingZoneData1, res.getParkingZones().get(0));
    assertEquals(expectParkingZoneData2, res.getParkingZones().get(1));

  }

}
