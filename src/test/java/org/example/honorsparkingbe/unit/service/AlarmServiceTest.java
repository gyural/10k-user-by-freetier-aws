package org.example.honorsparkingbe.unit.service;

import org.example.honorsparkingbe.domain.entity.AlarmEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.AlarmType;
import org.example.honorsparkingbe.repository.AlarmRepository;
import org.example.honorsparkingbe.repository.MemberRepository;
import org.example.honorsparkingbe.service.AlarmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AlarmService alarmService;

    private MemberEntity testMember;
    private AlarmEntity testAlarm;

    @BeforeEach
    void setUp() {
        testMember = new MemberEntity();
        testMember.setId(1L);
        testMember.setAuthId("testUser");

        testAlarm = new AlarmEntity();
        testAlarm.setId(1L);
        testAlarm.setMemberEntity(testMember);
        testAlarm.setAlarmType(AlarmType.INOUT);
        testAlarm.setContent("차량이 입차되었습니다.");
    }

    @Test
    void testFindMemberIdByAuthId_Success() {
        when(memberRepository.findByAuthId("testUser")).thenReturn(testMember);

        Long memberId = alarmService.findMemberIdByAuthId("testUser");

        assertNotNull(memberId);
        assertEquals(1L, memberId);
    }
}
