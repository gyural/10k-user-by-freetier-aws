//package org.example.honorsparkingbe.service;
//
//import jakarta.transaction.Transactional;
//import org.example.honorsparkingbe.domain.entity.CarEntity;
//import org.example.honorsparkingbe.domain.entity.MemberEntity;
//import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
//import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
//import org.example.honorsparkingbe.domain.enums.CarType;
//import org.example.honorsparkingbe.domain.enums.LoginPlatform;
//import org.example.honorsparkingbe.domain.enums.MemberRole;
//import org.example.honorsparkingbe.repository.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.time.LocalDateTime;
//
//@SpringBootTest
//@Transactional
//public class CurrentInfoServiceTest {
//
//    @Autowired
//    private ParkingHistoryRepository parkingHistoryRepository;
//    @Autowired private ParkingFeeRuleRepository parkingFeeRuleRepository;
//    @Autowired private MemberRepository memberRepository;
//    @Autowired private CarRepository carRepository;
//    @Autowired private ParkingZoneRepository parkingZoneRepository;
//    @Autowired private BCryptPasswordEncoder passwordEncoder;
//
//    @Autowired private CurrentInfoService currentInfoService;
//
//    private MemberEntity testMember1, testMember2, testMember3;
//    private CarEntity testCar1, testCar2, testCar3;
//    private ParkingZoneEntity testParkingZone1, testParkingZone2;
//    private ParkingHistoryEntity testParkingHistory;
//
//    @BeforeEach
//    void setUp() {
//        System.out.println("🔄 Before Each Test: 데이터 초기화");
//
//        /** 1️⃣ CarEntity 저장 **/
//        testCar1 = carRepository.save(CarEntity.builder()
//                .carNumber("12ga 3456")
//                .carType(CarType.COMPACT)
//                .isElectric(false)
//                // .entranceTime(LocalDateTime.of(2025, 2, 10, 8, 30))
//                .build());
//
//        testCar2 = carRepository.save(CarEntity.builder()
//                .carNumber("30na 6789")
//                .carType(CarType.MIDSIZE)
//                .isElectric(false)
//                // .entranceTime(LocalDateTime.of(2025, 2, 10, 9, 15))
//                .build());
//
//        testCar3 = carRepository.save(CarEntity.builder()
//                .carNumber("56da 9012")
//                .carType(CarType.FULLSIZE)
//                .isElectric(false)
//                // .entranceTime(LocalDateTime.of(2025, 2, 10, 9, 15))
//                .build());
//
//        /** 2️⃣ MemberEntity 저장 (BCrypt 적용) **/
//        testMember1 = memberRepository.save(MemberEntity.builder()
//                .authId("1111")
//                .email("user1@example.com")
//                .userName("TestUser1")
//                .password(passwordEncoder.encode("1111")) // ✅ 비밀번호 암호화
//                .role(MemberRole.ROLE_USER)
//                .phoneNumber("01012341234")
//                .birthday("01-01")
//                .birthdayYear(1990)
//                .loginPlatform(LoginPlatform.NORMAL)
//                .carEntity(testCar1) // ✅ CarEntity 연결
//                .build());
//
//        testMember2 = memberRepository.save(MemberEntity.builder()
//                .authId("2222")
//                .email("user2@example.com")
//                .userName("TestUser2")
//                .password(passwordEncoder.encode("2222")) // ✅ 비밀번호 암호화
//                .role(MemberRole.ROLE_USER)
//                .phoneNumber("01056785678")
//                .birthday("02-02")
//                .birthdayYear(1992)
//                .loginPlatform(LoginPlatform.NORMAL)
//                .carEntity(testCar2) // ✅ CarEntity 연결
//                .build());
//
//        testMember3 = memberRepository.save(MemberEntity.builder()
//                .authId("3333")
//                .email("user3@example.com")
//                .userName("TestUser3")
//                .password(passwordEncoder.encode("3333")) // ✅ 비밀번호 암호화
//                .role(MemberRole.ROLE_USER)
//                .phoneNumber("01056785678")
//                .birthday("02-02")
//                .birthdayYear(1992)
//                .loginPlatform(LoginPlatform.NORMAL)
//                .carEntity(testCar3) // ✅ CarEntity 연결
//                .build());
//
//
//        /** 4️⃣ ParkingHistoryEntity 저장 **/
//        testParkingHistory = parkingHistoryRepository.save(ParkingHistoryEntity.builder()
//                .memberEntity(testMember1)  // ✅ Member 연결
//                .carEntity(testCar1)        // ✅ Car 연결
//                .parkingZoneEntity(1) // ✅ ParkingZone 연결
//                .entranceTime(LocalDateTime.of(2025, 2, 10, 10, 0))
//                .exitTime(null)  // ✅ 현재 주차 중
//                .paymentType("NONE")
//                .build());
//
//        /** 5️⃣ ParkingFeeRuleEntity 저장 **/
//        parkingFeeRuleRepository.saveAll(List.of(
//                ParkingFeeRuleEntity.builder()
//                        .carType(CarType.COMPACT)
//                        .ruleName("Early Morning Rate")
//                        .startTime(0)
//                        .endTime(30)
//                        .costPerTimeSlot(500)
//                        .costTimeSlot(30)
//                        .parkingZoneEntity(testParkingZone1) // ✅ Zone A 연결
//                        .build(),
//
//                ParkingFeeRuleEntity.builder()
//                        .carType(CarType.COMPACT)
//                        .ruleName("Daytime Rate")
//                        .startTime(31)
//                        .endTime(120)
//                        .costPerTimeSlot(200)
//                        .costTimeSlot(10)
//                        .parkingZoneEntity(testParkingZone1) // ✅ Zone A 연결
//                        .build(),
//
//                ParkingFeeRuleEntity.builder()
//                        .carType(CarType.COMPACT)
//                        .ruleName("Night Rate")
//                        .startTime(121)
//                        .endTime(2147483647)
//                        .costPerTimeSlot(300)
//                        .costTimeSlot(10)
//                        .parkingZoneEntity(testParkingZone1) // ✅ Zone A 연결
//                        .build()
//        ));
//    }
//
//    @AfterEach
//    void tearDown() {
//        System.out.println("🗑️ After Each Test: 데이터 정리");
//        parkingHistoryRepository.deleteAll();
//        parkingFeeRuleRepository.deleteAll();
//        parkingZoneRepository.deleteAll();
//        carRepository.deleteAll();
//        memberRepository.deleteAll();
//    }
//
//    @Test
//    void testGetCurrentParkingInfo() {
//        // ✅ given: 테스트 대상 멤버 ID
//        Long memberId = testMember1.getId();
//
//        // ✅ when: 현재 주차 정보 조회
//        Map<String, Object> result = currentInfoService.getCurrentParkingInfo(memberId);
//
//        // ✅ then: 결과 검증
//        assertNotNull(result);
//        assertNotNull(result.get("parkingZone"));
//        assertEquals("Zone A", ((Map<?, ?>) result.get("parkingZone")).get("zoneName"));
//        assertEquals(testParkingHistory.getEntranceTime(), ((Map<?, ?>) result.get("parkingZone")).get("entranceTime"));
//
//        // ✅ 요금 관련 테스트 추가
//        assertNotNull(((Map<?, ?>) result.get("parkingZone")).get("cost"));
//        assertTrue((Integer) ((Map<?, ?>) result.get("parkingZone")).get("cost") >= 0);
//    }
//}
