package org.example.honorsparkingbe.unit.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.domain.entity.PayEntity;
import org.example.honorsparkingbe.dto.request.ParkingHistoryRequest;
import org.example.honorsparkingbe.dto.response.ParkingHistoryResponse;
import org.example.honorsparkingbe.dto.response.ParkingHistoryResponse.ParkingHistoryItem;
import org.example.honorsparkingbe.repository.ParkingHistoryRepository;
import org.example.honorsparkingbe.service.ParkingHistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;


@ExtendWith(MockitoExtension.class)
class ParkingHistoryServiceTest {

  @Mock
  private ParkingHistoryRepository parkingHistoryRepository;

  @InjectMocks
  private ParkingHistoryService parkingHistoryService;

  //페이징 처리 검증 테스트
  @Test
  void testGetParkingHistory_PageAndNumber_DefaultValues() {
    // given: page가 0 이하, number가 0인 요청 생성
    ParkingHistoryRequest request = new ParkingHistoryRequest();
    request.setPage(0);
    request.setNumber(0);
    request.setStartTime("202401010000");
    request.setEndTime("202412312359");

    // 빈 결과를 반환하도록 Mock 설정
    Page<ParkingHistoryEntity> mockPage = new PageImpl<>(Collections.emptyList(),
        PageRequest.of(0, 10), 0);
    when(parkingHistoryRepository.findByEntranceTimeBetween(
        any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class))
    ).thenReturn(mockPage);

    // when
    ParkingHistoryResponse response = parkingHistoryService.getParkingHistory(request);

    // then: page는 1, number는 10으로 기본 설정되어야 함
    assertNotNull(response);
    assertEquals(1, response.getPagination().getCurrentPage()); // 1부터 시작
    assertEquals(10, response.getPagination().getPageSize()); // 기본값 10
  }

  @Test
  void testGetParkingHistory_PageAndNumber_CustomValues() {
    // given: page가 3, number가 5인 요청
    ParkingHistoryRequest request = new ParkingHistoryRequest();
    request.setPage(3);
    request.setNumber(5);
    request.setStartTime("202401010000");
    request.setEndTime("202412312359");

    // 빈 결과를 반환하도록 Mock 설정
    Page<ParkingHistoryEntity> mockPage = new PageImpl<>(Collections.emptyList(),
        PageRequest.of(2, 5), 0);
    when(parkingHistoryRepository.findByEntranceTimeBetween(
        any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class))
    ).thenReturn(mockPage);

    // when
    ParkingHistoryResponse response = parkingHistoryService.getParkingHistory(request);

    // then: page는 3, number는 5로 설정되어야 함
    assertNotNull(response);
    assertEquals(3, response.getPagination().getCurrentPage()); // 기본적으로 1페이지부터 시작
    assertEquals(5, response.getPagination().getPageSize()); // 기본값 10이 적용됨
  }

  private LocalDateTime parseDateTime(String dateTime, LocalDateTime defaultValue) {
    if (dateTime == null || dateTime.isBlank()) {
      return defaultValue;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    return LocalDateTime.parse(dateTime, formatter);
  }

  //날짜 변환 검증 테스트
  @Test
  void testParseDateTime_ValidFormat() {
    LocalDateTime expected = LocalDateTime.of(2024, 3, 25, 14, 30);
    assertEquals(expected, parseDateTime("202403251430", null));
  }

  @Test
  void testParseDateTime_NullInput() {
    LocalDateTime defaultValue = LocalDateTime.of(2024, 1, 1, 0, 0);
    assertEquals(defaultValue, parseDateTime(null, defaultValue));
  }

  @Test
  void testParseDateTime_BlankInput() {
    LocalDateTime defaultValue = LocalDateTime.of(2024, 1, 1, 0, 0);
    assertEquals(defaultValue, parseDateTime("", defaultValue));
  }

  @Test
  void testParseDateTime_InvalidFormat() {
    assertThrows(Exception.class, () -> parseDateTime("2024-03-25 14:30", null));
  }

  //주차 이력 조회 로직 검증
  @Test
  public void testParkingHistoryItemMapping() {
    // Given: 요청 객체 생성
    ParkingHistoryRequest request = new ParkingHistoryRequest();
    request.setPage(1);   // 1페이지
    request.setNumber(5);  // 페이지 당 5개 항목
    request.setStartTime("202401010000");  // 시작 시간: 2024-01-01 00:00
    request.setEndTime("202412312359");    // 종료 시간: 2024-12-31 23:59

    // 변환된 날짜
    LocalDateTime EntranceTime = LocalDateTime.of(2024, 1, 1, 0, 0);
    LocalDateTime ExitTime = LocalDateTime.of(2024, 12, 31, 23, 59);

    // 가짜 주차 구역 (zoneName만 필요)
    ParkingZoneEntity zoneEntity1 = mock(ParkingZoneEntity.class);
    when(zoneEntity1.getZoneName()).thenReturn("A1");

    ParkingZoneEntity zoneEntity2 = mock(ParkingZoneEntity.class);
    when(zoneEntity2.getZoneName()).thenReturn("B2");

    // 결제 금액만 가져오기
    PayEntity payAmount1 = mock(PayEntity.class);
    when(payAmount1.getAmount()).thenReturn(1000);

    PayEntity payAmount2 = mock(PayEntity.class);
    when(payAmount2.getAmount()).thenReturn(2000);

    // Mocked ParkingHistoryEntity
    ParkingHistoryEntity history1 = mock(ParkingHistoryEntity.class);
    when(history1.getId()).thenReturn(1L);
    when(history1.getParkingZoneEntity()).thenReturn(zoneEntity1);
    when(history1.getEntranceTime()).thenReturn(EntranceTime);
    when(history1.getExitTime()).thenReturn(ExitTime);
    when(history1.getPayEntity()).thenReturn(payAmount1);

    ParkingHistoryEntity history2 = mock(ParkingHistoryEntity.class);
    when(history2.getId()).thenReturn(2L);
    when(history2.getParkingZoneEntity()).thenReturn(zoneEntity2);
    when(history2.getEntranceTime()).thenReturn(EntranceTime);
    when(history2.getExitTime()).thenReturn(ExitTime);
    when(history2.getPayEntity()).thenReturn(payAmount2);

    // Mocked Page of ParkingHistoryEntities
    Page<ParkingHistoryEntity> mockPage = mock(Page.class);
    when(mockPage.getContent()).thenReturn(Arrays.asList(history1, history2));
    when(mockPage.getNumber()).thenReturn(0);  // 첫 번째 페이지 (0부터 시작)
    when(mockPage.getTotalPages()).thenReturn(1);
    when(mockPage.getSize()).thenReturn(5);
    when(mockPage.getTotalElements()).thenReturn(2L);

    // When: Mock 설정
    when(parkingHistoryRepository.findByEntranceTimeBetween(
            any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
        .thenReturn(mockPage);

    // Act: 서비스 호출
    ParkingHistoryResponse response = parkingHistoryService.getParkingHistory(request);

    // DTO 변환 검증
    List<ParkingHistoryItem> parkingHistories = response.getParkingHistories();
    assertNotNull(parkingHistories); // null이 아님을 검증
    assertEquals(2, parkingHistories.size()); // 변환된 리스트 크기 검증 (2개)

    // 각 항목 검증
    ParkingHistoryItem item1 = parkingHistories.get(0);
    assertEquals(1L, item1.getId()); // 첫 번째 아이템의 ID 검증
    assertEquals("A1", item1.getZoneName()); // 첫 번째 아이템의 주차 구역 이름 검증
    assertEquals(1000, item1.getAmount()); // 첫 번째 아이템의 결제 금액 검증
    assertEquals(EntranceTime, item1.getEntranceTime()); // 첫 번째 아이템의 입차 시간 검증
    assertEquals(ExitTime, item1.getExitTime()); // 첫 번째 아이템의 출차 시간 검증

    ParkingHistoryItem item2 = parkingHistories.get(1);
    assertEquals(2L, item2.getId()); // 두 번째 아이템의 ID 검증
    assertEquals("B2", item2.getZoneName()); // 두 번째 아이템의 주차 구역 이름 검증
    assertEquals(2000, item2.getAmount()); // 두 번째 아이템의 결제 금액 검증
    assertEquals(EntranceTime, item2.getEntranceTime()); // 두 번째 아이템의 입차 시간 검증
    assertEquals(ExitTime, item2.getExitTime()); // 두 번째 아이템의 출차 시간 검증
  }
}










