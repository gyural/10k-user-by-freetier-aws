package org.example.honorsparkingbe.dummy.createData;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.domain.entity.CityEntity;
import org.example.honorsparkingbe.domain.entity.DistrictEntity;
import org.example.honorsparkingbe.domain.entity.EupMyeonDongEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.repository.CityRepository;
import org.example.honorsparkingbe.repository.DistrictRepository;
import org.example.honorsparkingbe.repository.EupMyeonDongRepository;
import org.example.honorsparkingbe.repository.ParkingZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateParkingZoneList {

    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    private final EupMyeonDongRepository eupMyeonDongRepository;
    private final ParkingZoneRepository parkingZoneRepository;

    @PostConstruct
    @Transactional
    public void insertDummyData() {
        // City 데이터 추가 (중복 체크)
        CityEntity seoul = cityRepository.findByName("서울").orElse(CityEntity.builder().name("서울").build());
        CityEntity busan = cityRepository.findByName("부산").orElse(CityEntity.builder().name("부산").build());
        CityEntity daejeon = cityRepository.findByName("대전").orElse(CityEntity.builder().name("대전").build());

        cityRepository.save(seoul);
        cityRepository.save(busan);
        cityRepository.save(daejeon);

        // District 데이터 추가 (중복 체크)
        DistrictEntity gangnam = districtRepository.findByName("강남구").orElse(DistrictEntity.builder().name("강남구").build());
        DistrictEntity haeundae = districtRepository.findByName("해운대구").orElse(DistrictEntity.builder().name("해운대구").build());
        DistrictEntity seo = districtRepository.findByName("서구").orElse(DistrictEntity.builder().name("서구").build());

        districtRepository.save(gangnam);
        districtRepository.save(haeundae);
        districtRepository.save(seo);

        // EupMyeonDong 데이터 추가 (중복 체크)
        EupMyeonDongEntity yeoksam = eupMyeonDongRepository.findByName("역삼동").orElse(EupMyeonDongEntity.builder().name("역삼동").build());
        EupMyeonDongEntity centum = eupMyeonDongRepository.findByName("센텀시티").orElse(EupMyeonDongEntity.builder().name("센텀시티").build());
        EupMyeonDongEntity doma = eupMyeonDongRepository.findByName("도마동").orElse(EupMyeonDongEntity.builder().name("도마동").build());

        eupMyeonDongRepository.save(yeoksam);
        eupMyeonDongRepository.save(centum);
        eupMyeonDongRepository.save(doma);

        // ParkingZone 데이터 추가
        ParkingZoneEntity parkingZone1 = ParkingZoneEntity.builder()
                .zoneName("서울 강남구 역삼동 주차장")
                .size(10)
                .latitude(37.4979)
                .longitude(127.0276)
                .cityEntity(seoul)
                .districtEntity(gangnam)
                .eupMyeonDongEntity(yeoksam)
                .build();

        ParkingZoneEntity parkingZone2 = ParkingZoneEntity.builder()
                .zoneName("부산 해운대구 센텀시티 주차장")
                .size(5)
                .latitude(35.1710)
                .longitude(129.1213)
                .cityEntity(busan)
                .districtEntity(haeundae)
                .eupMyeonDongEntity(centum)
                .build();

        ParkingZoneEntity parkingZone3 = ParkingZoneEntity.builder()
                .zoneName("대전 서구 도마동 주차장")
                .size(3)
                .latitude(36.3510)
                .longitude(127.3845)
                .cityEntity(daejeon)
                .districtEntity(seo)
                .eupMyeonDongEntity(doma)
                .build();

        // 중복 체크 후 주차장 저장
        if (!parkingZoneRepository.existsByZoneName(parkingZone1.getZoneName())) {
            parkingZoneRepository.save(parkingZone1);
        }
        if (!parkingZoneRepository.existsByZoneName(parkingZone2.getZoneName())) {
            parkingZoneRepository.save(parkingZone2);
        }
        if (!parkingZoneRepository.existsByZoneName(parkingZone3.getZoneName())) {
            parkingZoneRepository.save(parkingZone3);
        }
    }
}
