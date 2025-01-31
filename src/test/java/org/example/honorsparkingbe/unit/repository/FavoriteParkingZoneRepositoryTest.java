package org.example.honorsparkingbe.unit.repository;

import org.example.honorsparkingbe.domain.entity.*;
import org.example.honorsparkingbe.domain.enums.LoginPlatform;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:config/application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FavoriteParkingZoneRepositoryTest {

    @Autowired
    private FavoriteParkingZoneRepository favoriteParkingZoneRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ParkingZoneRepository parkingZoneRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private EupMyeonDongRepository eupMyeonDongRepository;

    private CarEntity sampleCar1;
    private CarEntity sampleCar2;

    private MemberEntity memberWith3Favorite;
    private MemberEntity memberWith0Favorite;

    private CityEntity seoul;
    private CityEntity busan;
    private CityEntity daejeon;

    private DistrictEntity sampleDistrict;
    private EupMyeonDongEntity sampleEupMyeonDong;

    private ParkingZoneEntity seoulParking;
    private ParkingZoneEntity busanParking;
    private ParkingZoneEntity daejeonParking;

    private FavoriteParkingZoneEntity favoriteParkingZone1;
    private FavoriteParkingZoneEntity favoriteParkingZone2;
    private FavoriteParkingZoneEntity favoriteParkingZone3;

    @BeforeEach
    void setUp() {
        // 도시 엔티티 저장
        seoul = cityRepository.save(CityEntity.builder().name("서울").build());
        busan = cityRepository.save(CityEntity.builder().name("부산").build());
        daejeon = cityRepository.save(CityEntity.builder().name("대전").build());

        // 구역 엔티티 저장
        sampleDistrict = districtRepository.save(DistrictEntity.builder().name("중구").build());
        sampleEupMyeonDong = eupMyeonDongRepository.save(EupMyeonDongEntity.builder().name("데브몬동").build());

        // 주차장 엔티티 설정 후 저장
        seoulParking = parkingZoneRepository.save(
                ParkingZoneEntity.builder()
                        .cityEntity(seoul)
                        .districtEntity(sampleDistrict)
                        .eupMyeonDongEntity(sampleEupMyeonDong)
                        .build()
        );
        busanParking = parkingZoneRepository.save(
                ParkingZoneEntity.builder()
                        .cityEntity(busan)
                        .districtEntity(sampleDistrict)
                        .eupMyeonDongEntity(sampleEupMyeonDong)
                        .build()
        );
        daejeonParking = parkingZoneRepository.save(
                ParkingZoneEntity.builder()
                        .cityEntity(daejeon)
                        .districtEntity(sampleDistrict)
                        .eupMyeonDongEntity(sampleEupMyeonDong)
                        .build()
        );

        // 자동차 엔티티 저장
        sampleCar1 = carRepository.save(CarEntity.builder().carNumber("sampleCarNumber1").build());
        sampleCar2 = carRepository.save(CarEntity.builder().carNumber("sampleCarNumber2").build());

        // 멤버 엔티티 저장
        memberWith3Favorite = memberRepository.save(
                MemberEntity.builder()
                        .carEntity(sampleCar1)
                        .userName("3bookMarkUser")
                        .authId("authId1")
                        .password("password1")
                        .phoneNumber("01012345678")
                        .email("user1@example.com")
                        .birthdayYear(1990)
                        .birthday("0101")
                        .loginPlatform(LoginPlatform.GOOGLE)
                        .role(MemberRole.USER)
                        .build()
        );

        memberWith0Favorite = memberRepository.save(
                MemberEntity.builder()
                        .carEntity(sampleCar2)
                        .userName("0bookMarkUser")
                        .authId("authId2")
                        .password("password2")
                        .phoneNumber("01087654321")
                        .email("user2@example.com")
                        .birthdayYear(1995)
                        .birthday("0202")
                        .loginPlatform(LoginPlatform.GOOGLE)
                        .role(MemberRole.USER)
                        .build()
        );

        // FavoriteParkingZone 엔티티 저장
        favoriteParkingZone1 = favoriteParkingZoneRepository.save(
                FavoriteParkingZoneEntity.builder()
                        .memberEntity(memberWith3Favorite)
                        .parkingZoneEntity(seoulParking)
                        .build()
        );
        favoriteParkingZone2 = favoriteParkingZoneRepository.save(
                FavoriteParkingZoneEntity.builder()
                        .memberEntity(memberWith3Favorite)
                        .parkingZoneEntity(busanParking)
                        .build()
        );
        favoriteParkingZone3 = favoriteParkingZoneRepository.save(
                FavoriteParkingZoneEntity.builder()
                        .memberEntity(memberWith3Favorite)
                        .parkingZoneEntity(daejeonParking)
                        .build()
        );
    }

    @Test
    @DisplayName("Member Id를 통한 즐겨찾는 주차장 불러오기 레포지토리 메서드 테스트")
    void testFindAllFavoriteParkingZonesByMemberId() {
        // when:
        // memberWith3Favorite의 ID를 통해 즐겨찾는 주차장 조회
        // memberWith0Favorite의 ID를 통해 즐겨찾는 주차장 조회
        Long memberId1 = memberWith3Favorite.getId();
        Long memberId2 = memberWith0Favorite.getId();
        List<FavoriteParkingZoneEntity> result1 = favoriteParkingZoneRepository.findAllByMemberEntity_Id(memberId1);
        List<FavoriteParkingZoneEntity> result2 = favoriteParkingZoneRepository.findAllByMemberEntity_Id(memberId2);

        // then
        // 3개가 모두 정확하게 나와야함
        assertThat(result1).hasSize(3);
        assertThat(result1)
                .extracting("parkingZoneEntity")
                .containsExactlyInAnyOrder(seoulParking, busanParking, daejeonParking);

        // then
        // 0개가 모두 나와야함
        assertThat(result2).hasSize(0);
    }
}
