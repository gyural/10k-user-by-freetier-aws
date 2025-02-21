//package org.example.honorsparkingbe.unit.repository;
//
//import org.example.honorsparkingbe.domain.entity.AlarmEntity;
//import org.example.honorsparkingbe.domain.entity.MemberEntity;
//import org.example.honorsparkingbe.domain.enums.AlarmType;
//import org.example.honorsparkingbe.domain.enums.IsRead;
//import org.example.honorsparkingbe.repository.AlarmRepository;
//import org.example.honorsparkingbe.repository.MemberRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//
//@DataJpaTest
//@ActiveProfiles("test")
//@TestPropertySource(locations = "classpath:config/application-test.yml")
////@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class AlarmRepositoryTest {
//
//    @Autowired
//    private AlarmRepository alarmRepository;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    private MemberEntity testMember;
//
//    @BeforeEach
//    void setUp() {
//        // 테스트용 회원 저장
//        testMember = memberRepository.save(
//                MemberEntity.builder()
//                        .authId("testAuthId")
//                        .build()
//        );
//
//        // 테스트용 알람 2개 저장
//        alarmRepository.saveAll(List.of(
//                createAlarm(testMember, "첫 번째 알람", IsRead.UNREAD, AlarmType.INOUT),
//                createAlarm(testMember, "두 번째 알람", IsRead.READ, AlarmType.RESERVE)
//        ));
//    }
//
//    /**
//     * 1. 특정 회원의 알람 조회 (페이지네이션 포함)
//     */
//    @Test
//    @DisplayName("특정 회원의 알람 조회")
//    void testFindByMemberEntityId() {
//        // When
//        List<AlarmEntity> alarms = alarmRepository.findByMemberEntityId(testMember.getId(), null).getContent();
//
//        // Then
//        assertThat(alarms).hasSize(2);
//        assertThat(alarms.get(0).getContent()).isEqualTo("첫 번째 알람");
//    }
//
//    /**
//     * 2. 특정 회원의 특정 알람 유형 조회
//     */
//    @Test
//    @DisplayName("특정 회원의 특정 알람 유형 조회")
//    void testFindByMemberEntityIdAndAlarmType() {
//        // When
//        List<AlarmEntity> alarms = alarmRepository.findByMemberEntityIdAndAlarmType(testMember.getId(), AlarmType.INOUT, null).getContent();
//
//        // Then
//        assertThat(alarms).hasSize(1);
//        assertThat(alarms.get(0).getAlarmType()).isEqualTo(AlarmType.INOUT);
//    }
//
//    /**
//     * 3. 읽음 처리된 알람 조회
//     */
//    @Test
//    @DisplayName("읽음 처리된 알람 조회")
//    void testFindReadAlarms() {
//        // Given
//        List<Long> alarmIds = alarmRepository.findAll().stream().map(AlarmEntity::getId).toList();
//
//        // When
//        List<Long> readAlarms = alarmRepository.findReadAlarms(alarmIds);
//
//        // Then
//        assertThat(readAlarms).hasSize(1);
//    }
//
//    /**
//     * 4. 여러 개의 알람 읽음 처리
//     */
//    @Test
//    @DisplayName("알람 읽음 처리")
//    void testUpdateAlarmsToRead() {
//        // Given
//        List<Long> unreadAlarmIds = alarmRepository.findAll().stream()
//                .filter(alarm -> alarm.getIsRead() == IsRead.UNREAD)
//                .map(AlarmEntity::getId)
//                .toList();
//
//        // When
//        int updatedCount = alarmRepository.updateAlarmsToRead(unreadAlarmIds);
//
//        // Then
//        assertThat(updatedCount).isEqualTo(1); // UNREAD → READ 처리된 건 1개
//    }
//
//    /**
//     * 5. 특정 알람 삭제
//     */
//    @Test
//    @DisplayName("특정 알람 삭제")
//    void testDeleteAlarms() {
//        // Given
//        List<Long> alarmIds = alarmRepository.findAll().stream().map(AlarmEntity::getId).toList();
//
//        // When
//        int deletedCount = alarmRepository.deleteAlarms(alarmIds);
//
//        // Then
//        assertThat(deletedCount).isEqualTo(2); // 2개 삭제 성공
//        assertThat(alarmRepository.findAll()).isEmpty(); // 삭제 후 남은 데이터가 없음
//    }
//
//    /**
//     * 6. 특정 회원의 모든 알람 삭제
//     */
//    @Test
//    @DisplayName("특정 회원의 모든 알람 삭제")
//    void testDeleteAllAlarmsByMemberId() {
//        // When
//        int deletedCount = alarmRepository.deleteAllAlarmsByMemberId(testMember.getId());
//
//        // Then
//        assertThat(deletedCount).isEqualTo(2);
//        assertThat(alarmRepository.findByMemberEntityId(testMember.getId(), null).getTotalElements()).isEqualTo(0);
//    }
//
//    /**
//     * 도우미 메서드 - 알람 생성
//     */
//    private AlarmEntity createAlarm(MemberEntity member, String content, IsRead isRead, AlarmType alarmType) {
//        AlarmEntity alarm = new AlarmEntity();
//        alarm.setMemberEntity(member);
//        alarm.setContent(content);
//        alarm.setIsRead(isRead);
//        alarm.setAlarmType(alarmType);
//        alarm.setCreatedAt(LocalDateTime.now());
//        return alarmRepository.save(alarm);
//    }
//
//}
