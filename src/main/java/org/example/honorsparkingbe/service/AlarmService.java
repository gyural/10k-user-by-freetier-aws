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
import java.util.Map;

@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;

    public AlarmService(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

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
}
