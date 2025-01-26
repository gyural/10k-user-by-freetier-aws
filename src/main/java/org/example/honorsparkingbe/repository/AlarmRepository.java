package org.example.honorsparkingbe.repository;

import org.example.honorsparkingbe.domain.entity.AlarmEntity;
import org.example.honorsparkingbe.domain.enums.AlarmType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {

    // 특정 회원의 모든 알람 조회
    Page<AlarmEntity> findByMemberEntityId(Long memberId, Pageable pageable);

    // 특정 회원의 특정 알람 유형 조회
    Page<AlarmEntity> findByMemberEntityIdAndAlarmType(Long memberId, AlarmType alarmType, Pageable pageable);
}
