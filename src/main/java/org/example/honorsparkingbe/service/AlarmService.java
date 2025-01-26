package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.domain.entity.AlarmEntity;
import org.example.honorsparkingbe.domain.enums.AlarmType;
import org.example.honorsparkingbe.repository.AlarmRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;

    public AlarmService(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    // 회원 알람 불러오기
    // GET /api/v1/alarmAll
    public Map<String, Object> getAlarms(Long memberId, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<AlarmEntity> alarmPage;
        if (category != null) {
            boolean isValidCategory = Arrays.stream(AlarmType.values())
                    .anyMatch(type -> type.name().equalsIgnoreCase(category));
            if (!isValidCategory) {
                throw new IllegalArgumentException("Invalid category value: " + category);
            }
            AlarmType alarmType = AlarmType.valueOf(category.toUpperCase());
            alarmPage = alarmRepository.findByMemberEntityIdAndAlarmType(memberId, alarmType, pageable);
        } else {
            alarmPage = alarmRepository.findByMemberEntityId(memberId, pageable);
        }

        return Map.of(
                "alarms", alarmPage.getContent(),
                "pagination", Map.of(
                        "currentPage", alarmPage.getNumber(), // 내부에서 0부터 시작
                        "totalPages", alarmPage.getTotalPages(),
                        "pageSize", alarmPage.getSize(),
                        "totalItems", alarmPage.getTotalElements()
                )
        );
    }

    // 회원 알람 읽기
    // PUT /api/v1/alarm
    public Map<String, Object> updateAlarmsToRead(List<Long> alarmIDList) {
        if (alarmIDList == null || alarmIDList.isEmpty()) {
            throw new IllegalArgumentException("alarmIDList cannot be null or empty");
        }

        // 업데이트 실행
        int updatedCount = alarmRepository.updateAlarmsToRead(alarmIDList);

        // 응답 데이터 구성
        return Map.of(
                "success", updatedCount > 0,
                "updatedIds", alarmIDList
        );
    }
}
