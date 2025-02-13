package org.example.honorsparkingbe.unit.repository;

import org.example.honorsparkingbe.domain.entity.AlarmEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.AlarmType;
import org.example.honorsparkingbe.repository.AlarmRepository;
import org.example.honorsparkingbe.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Rollback
class AlarmRepositoryTest {

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testSaveAndFindAlarm() {
        // Given
        MemberEntity member = new MemberEntity();
        member.setAuthId("testUser");
        member.setId(1L);
        memberRepository.save(member);

        AlarmEntity alarm = new AlarmEntity();
        alarm.setAlarmType(AlarmType.INOUT);
        alarm.setContent("테스트 알람");
        alarm.setCreatedAt(LocalDateTime.now());
        alarm.setMemberEntity(member);
        alarmRepository.save(alarm);

        // When
        List<AlarmEntity> foundAlarms = alarmRepository.findByMemberEntityId(member.getId());

        // Then
        assertNotNull(foundAlarms);
        assertFalse(foundAlarms.isEmpty());
    }
}
