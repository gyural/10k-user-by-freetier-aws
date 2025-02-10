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
        CityEntity seoul = cityRepository.findByName("Seoul").orElse(CityEntity.builder().name("Seoul").build());
        CityEntity busan = cityRepository.findByName("Busan").orElse(CityEntity.builder().name("Busan").build());
        CityEntity daejeon = cityRepository.findByName("Daejeon").orElse(CityEntity.builder().name("Daejeon").build());

        cityRepository.save(seoul);
        cityRepository.save(busan);
        cityRepository.save(daejeon);

        // District 데이터 추가 (중복 체크)
        DistrictEntity gangnam = districtRepository.findByName("Gangnam-gu").orElse(DistrictEntity.builder().name("Gangnam-gu").build());
        DistrictEntity haeundae = districtRepository.findByName("Haeundae-gu").orElse(DistrictEntity.builder().name("Haeundae-gu").build());
        DistrictEntity seo = districtRepository.findByName("Seo-gu").orElse(DistrictEntity.builder().name("Seo-gu").build());

        districtRepository.save(gangnam);
        districtRepository.save(haeundae);
        districtRepository.save(seo);

        // EupMyeonDong 데이터 추가 (중복 체크)
        EupMyeonDongEntity yeoksam = eupMyeonDongRepository.findByName("Yeoksam-dong").orElse(EupMyeonDongEntity.builder().name("Yeoksam-dong").build());
        EupMyeonDongEntity centum = eupMyeonDongRepository.findByName("Centum City").orElse(EupMyeonDongEntity.builder().name("Centum City").build());
        EupMyeonDongEntity doma = eupMyeonDongRepository.findByName("Doma-dong").orElse(EupMyeonDongEntity.builder().name("Doma-dong").build());

        eupMyeonDongRepository.save(yeoksam);
        eupMyeonDongRepository.save(centum);
        eupMyeonDongRepository.save(doma);

        // ParkingZone 데이터 추가
        ParkingZoneEntity parkingZone1 = ParkingZoneEntity.builder()
                .zoneName("Seoul Gangnam-gu Yeoksam-dong Parking Lot")
                .size(10)
                .latitude(37.4979)
                .longitude(127.0276)
                .cityEntity(seoul)
                .districtEntity(gangnam)
                .eupMyeonDongEntity(yeoksam)
                .build();

        ParkingZoneEntity parkingZone2 = ParkingZoneEntity.builder()
                .zoneName("Busan Haeundae-gu Centum City Parking Lot")
                .size(5)
                .latitude(35.1710)
                .longitude(129.1213)
                .cityEntity(busan)
                .districtEntity(haeundae)
                .eupMyeonDongEntity(centum)
                .build();

        ParkingZoneEntity parkingZone3 = ParkingZoneEntity.builder()
                .zoneName("Daejeon Seo-gu Doma-dong Parking Lot")
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
