package org.example.honorsparkingbe.unit.repository;

import org.example.honorsparkingbe.domain.entity.CityEntity;
import org.example.honorsparkingbe.domain.entity.DistrictEntity;
import org.example.honorsparkingbe.domain.entity.EupMyeonDongEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.repository.CityRepository;
import org.example.honorsparkingbe.repository.DistrictRepository;
import org.example.honorsparkingbe.repository.EupMyeonDongRepository;
import org.example.honorsparkingbe.repository.ParkingZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:config/application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ParkingZoneRepositoryTest {

    @Autowired
    private ParkingZoneRepository parkingZoneRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    DistrictRepository districtRepository;

    @Autowired
    EupMyeonDongRepository eupMyeonDongRepository;

    private ParkingZoneEntity seoulParking;
    private ParkingZoneEntity busanParking;
    private ParkingZoneEntity daejeonParking;


    @BeforeEach
    void setUp() {
        CityEntity seoul = new CityEntity();
        seoul.setName("서울");
        cityRepository.save(seoul);

        CityEntity busan = new CityEntity();
        busan.setName("부산");
        cityRepository.save(busan);

        CityEntity daejeon = new CityEntity();
        daejeon.setName("대전");
        cityRepository.save(daejeon);

        DistrictEntity sampleDistrict = new DistrictEntity();
        sampleDistrict.setName("중구");
        districtRepository.save(sampleDistrict);

        EupMyeonDongEntity sampleEupMyeonDong = new EupMyeonDongEntity();
        sampleEupMyeonDong.setName("데브몬동");
        eupMyeonDongRepository.save(sampleEupMyeonDong);

        // 서울 주차장
        seoulParking = new ParkingZoneEntity();
        seoulParking.setLatitude(37.5665f);  // 서울 위도
        seoulParking.setLongitude(126.9780f); // 서울 경도
        seoulParking.setZoneName("서울 주차장");
        seoulParking.setSize(100);
        seoulParking.setMaxCost(12000);
        seoulParking.setAddress("서울특별시");

        seoulParking.setCityEntity(seoul);
        seoulParking.setDistrictEntity(sampleDistrict);
        seoulParking.setEupMyeonDongEntity(sampleEupMyeonDong);

        // 부산 주차장
        busanParking = new ParkingZoneEntity();
        busanParking.setLatitude(35.1796f); // 부산 위도
        busanParking.setLongitude(129.0756f); // 부산 경도
        busanParking.setZoneName("부산 주차장");
        busanParking.setSize(80);
        busanParking.setMaxCost(10000);
        busanParking.setAddress("부산광역시");

        busanParking.setCityEntity(busan);
        busanParking.setDistrictEntity(sampleDistrict);
        busanParking.setEupMyeonDongEntity(sampleEupMyeonDong);

        // 대전 주차장
        daejeonParking = new ParkingZoneEntity();
        daejeonParking.setLatitude(36.3504f); // 대전 위도
        daejeonParking.setLongitude(127.3845f); // 대전 경도
        daejeonParking.setZoneName("대전 주차장");
        daejeonParking.setSize(50);
        daejeonParking.setMaxCost(8000);
        daejeonParking.setAddress("대전광역시");

        daejeonParking.setCityEntity(daejeon);
        daejeonParking.setDistrictEntity(sampleDistrict);
        daejeonParking.setEupMyeonDongEntity(sampleEupMyeonDong);

        parkingZoneRepository.saveAll(List.of(seoulParking, busanParking, daejeonParking));
    }

    @Test
    @DisplayName("위경도 기준 가까운 거리순 정렬 확인")
    void testFindClosestParkingZones() {
        double latitude = 37.5665; // 서울 기준
        double longitude = 126.9780;
        int limit = 3;

        List<ParkingZoneEntity> result = parkingZoneRepository.findClosestParkingZones(latitude, longitude, limit);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getZoneName()).isEqualTo("서울 주차장");  // 가장 가까운 곳
        assertThat(result.get(1).getZoneName()).isEqualTo("대전 주차장");  // 두 번째 가까운 곳
        assertThat(result.get(2).getZoneName()).isEqualTo("부산 주차장");  // 가장 먼 곳
    }

    @Test
    @DisplayName("ID 배열로 주차장 리스트 반환 테스트")
    void testFindAllByIdIn() {
        // 저장된 주차장의 ID 리스트 가져오기
        List<Long> ids = parkingZoneRepository.findAll().stream()
                .map(ParkingZoneEntity::getId)
                .collect(Collectors.toList());

        // ID 목록으로 주차장 조회
        List<ParkingZoneEntity> result = parkingZoneRepository.findAllByIdIn(ids);

        assertThat(result).hasSize(3);  // 3개가 반환되어야 함
        assertThat(result).extracting(ParkingZoneEntity::getZoneName)
                .containsExactlyInAnyOrder("서울 주차장", "부산 주차장", "대전 주차장");
    }

}
