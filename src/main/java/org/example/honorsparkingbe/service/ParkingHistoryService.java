package org.example.honorsparkingbe.service;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.example.honorsparkingbe.dto.request.ParkingHistoryRequest;
import org.example.honorsparkingbe.dto.response.ParkingHistoryResponse;
import org.example.honorsparkingbe.dto.response.ParkingHistoryResponse.ParkingHistoryItem;
import org.example.honorsparkingbe.dto.response.PaginationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Set;
import org.example.honorsparkingbe.dto.DeleteParkingHistoryDTO;
import org.example.honorsparkingbe.dto.response.ParkingHistoryDeleteResponse;
import org.example.honorsparkingbe.repository.internal.ParkingHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ParkingHistoryService {

    private final ParkingHistoryRepository parkingHistoryRepository;

    public ParkingHistoryResponse getParkingHistory(ParkingHistoryRequest request) {
        // 기본값 설정
        int page = request.getPage() > 0 ? request.getPage() - 1 : 0; // PageRequest는 0부터 시작 삼항연산자
        int size = request.getNumber() > 0 ? request.getNumber() : 10;

        LocalDateTime startTime = request.getStartTime() != null
            ? LocalDateTime.parse(request.getStartTime())
            : LocalDateTime.now().minusYears(50);  // 기본값: 50년 전부터 조회

        LocalDateTime endTime = request.getEndTime() != null
            ? LocalDateTime.parse(request.getEndTime())
            : LocalDateTime.now().plusYears(50);  // 기본값: 50년 후까지 조회

        // 주차 이력 조회
        Page<ParkingHistoryEntity> parkingHistoryPage = parkingHistoryRepository
            .findByEntranceTimeBetween(startTime, endTime, PageRequest.of(page, size));


        // DTO 변환
        List<ParkingHistoryItem> parkingHistories = parkingHistoryPage.getContent().stream()
            .map(history -> new ParkingHistoryItem(
                history.getId(),
                history.getParkingZoneEntity() != null ? history.getParkingZoneEntity().getZoneName() : "알 수 없음", // null 처리
                history.getEntranceTime() != null ? history.getEntranceTime() : null, // null 그대로 유지 (혹은 LocalDateTime.MIN)
                history.getExitTime() != null ? history.getExitTime() : null, // null 그대로 유지 (혹은 "출차 기록 없음")
                history.getPayEntity() != null ? history.getPayEntity().getAmount() : 0 // null 처리 후 기본값 0
            ))
            .collect(Collectors.toList());
        System.out.println(parkingHistories.size());
        // 페이징 정보 생성
        PaginationResponse pagination = new PaginationResponse(
                parkingHistoryPage.getNumber() + 1,  // 현재 페이지 (1부터 시작)
                parkingHistoryPage.getTotalPages(),  // 전체 페이지 수
                parkingHistoryPage.getSize(),        // 한 페이지에 포함된 아이템 수
                parkingHistoryPage.getTotalElements() // 총 아이템 개수
        );

        // 최종 응답 객체 생성
        return new ParkingHistoryResponse(parkingHistories, pagination);
    }

    /**
     *
     * @param dateTime
     * @param defaultValue
     * @return 날짜 변환 (YYYYMMDDHHMM → LocalDateTime)
     */
    private LocalDateTime parseDateTime(String dateTime, LocalDateTime defaultValue) {
        if (dateTime == null || dateTime.isBlank()) {
            return defaultValue;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        return LocalDateTime.parse(dateTime, formatter);
    }


    /**
     * @param deleteParkingHistoryDTO
     * @return
     */
    @Transactional
    public ParkingHistoryDeleteResponse softDeleteParkingHistories(
            DeleteParkingHistoryDTO deleteParkingHistoryDTO) {
        // 1. 유저 정보 가져오기
        List<Long> targetIds = deleteParkingHistoryDTO.getParingHistoryDeleteRequest()
                .getHistoryIDList();
        Long userId = deleteParkingHistoryDTO.getUserId();

        // 2. 해당 ID에 대한 권한 확인
        List<ParkingHistoryEntity> userHistories = parkingHistoryRepository.findByIdsAndMember(
                targetIds, userId);
        // 본인의 이력이 맞는지 검증

        // 3. 본인 ID가 아닌 것은 필터링
        Set<Long> validIds = userHistories.stream()
                .map(ParkingHistoryEntity::getId)
                .collect(Collectors.toSet());

        List<Long> deletedIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();

        for (Long id : targetIds) {
            if (validIds.contains(id)) {
                deletedIds.add(id); // 삭제 성공 리스트에 추가
            } else {
                failedIds.add(id); // 삭제 실패 리스트에 추가
            }
        }

        // 4. soft delete sql수행
        parkingHistoryRepository.softDeleteAtByIds(LocalDateTime.now(), deletedIds);

        return ParkingHistoryDeleteResponse.builder()
                .isSuccess(targetIds.size() == deletedIds.size())
                .deletedIds(deletedIds)
                .failedIds(failedIds)
                .build();

    }
}


