package org.example.honorsparkingbe.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.example.honorsparkingbe.dto.GetParkingZoneByKeywordDTO;
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
    assertEquals(res.getMeta().getPaginationResponse().getCurrentPage(), 0);
    assertEquals(res.getMeta().getPaginationResponse().getPageSize(), 10);
    assertEquals(res.getMeta().getPaginationResponse().getTotalItems(), 0);
    assertEquals(res.getMeta().getPaginationResponse().getTotalPages(), 0);

  }

  @Test
  @DisplayName("주차장 이름만 키워드와 일치할때 반환 확인")
  void keywordMatchedOnlyZoneNameTest() {
    //TODO
  }

  @Test
  @DisplayName("주차장 이름 제외 나머지 하나(City)만 일치할 때 반환 확인")
  void keywordMatchedOnlyCityNameTest() {
    //TODO

  }

  @Test
  @DisplayName("모든 필드가 일치할 때 반환 확인")
  void keywordNotMatchedAllFieldTest() {
    //TODO

  }

  @Test
  @DisplayName("키워드 매칭된 주차장이 pageSize가 넘어가고 1이상 페이지 요청 시 반환 확인")
  void keywordMatchedPageSizeTest() {
    //TODO

  }

  @Test
  @DisplayName("키워드 매칭된 주차장의 즐겨찾기가 잘 반영되는지 확인")
  void keywordNotMatchedBookMarkTest() {
    //TODO

  }

}
