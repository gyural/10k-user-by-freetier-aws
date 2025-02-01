package org.example.honorsparkingbe.service;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.domain.entity.FavoriteParkingZoneEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.dto.ParkingFeeRuleDTO;
import org.example.honorsparkingbe.dto.ParkingZoneDTO;
import org.example.honorsparkingbe.dto.request.ParkingZoneListRequest;
import org.example.honorsparkingbe.dto.response.ParkingZoneListResponse;
import org.example.honorsparkingbe.repository.FavoriteParkingZoneRepository;
import org.example.honorsparkingbe.repository.ParkingFeeRuleRepository;
import org.example.honorsparkingbe.repository.ParkingZoneRepository;
import org.example.honorsparkingbe.util.ParkingFeeRuleDTOConverter;
import org.example.honorsparkingbe.util.ParkingZoneDTOConverter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParkingZoneInfoService {

    //Repository
    private final FavoriteParkingZoneRepository favoriteParkingZoneRepository;
    private final ParkingZoneRepository parkingZoneRepository;
    private final ParkingFeeRuleRepository parkingFeeRuleRepository;
    //Util
    private final ParkingZoneDTOConverter parkingZoneDTOConverter;
    private final ParkingFeeRuleDTOConverter parkingFeeRuleDTOConverter;

    public ParkingZoneListResponse getParkingZones(ParkingZoneListRequest request) {
        Long page = request.getPage() != null ? request.getPage() : 0; // 기본값 0
        int pagePerItems = 10; // 한 페이지에 보여줄 개수

        // 1️⃣ 즐겨찾기 주차장 가져오기
        List<FavoriteParkingZoneEntity> favoriteParkingZones = favoriteParkingZoneRepository.findAllByMemberEntity_IdOrderByIdAsc(request.getMemberID());

        // 즐겨찾기 주차장을 ParkingZoneEntity로 변환
        List<ParkingZoneEntity> favoriteZones = favoriteParkingZones.stream()
                .map(FavoriteParkingZoneEntity::getParkingZoneEntity)
                .collect(Collectors.toList());

        List<Long> favoriteZonesIds = favoriteZones.stream().map(ParkingZoneEntity::getId).collect(Collectors.toList());
        long favoriteCount = favoriteZones.size();
        // 일반 주차장이 필요한 개수
        long remainingSlots = Math.max(0, pagePerItems - favoriteCount); // 최소값 0으로 설정

        // 최대 호출 개수가 10보다 커지지 않도록 설정
        remainingSlots = Math.min(remainingSlots, 10); // 최대값 10으로 제한

        // 2️⃣ 일반 주차장 가져오기 (남은 슬롯만큼)
        List<ParkingZoneEntity> nonFavoriteParkingZones;

        if (remainingSlots > 0) {
            nonFavoriteParkingZones = parkingZoneRepository.findClosestParkingZonesWithExclusion(
                    request.getLatitude(),
                    request.getLongitude(),
                    remainingSlots,  // ⭐ 제한 개수 설정
                    page * 10,
                    favoriteZonesIds
            );
            System.out.println(nonFavoriteParkingZones.size());
            System.out.println("Latitude: " + request.getLatitude());
            System.out.println("Longitude: " + request.getLongitude());
            System.out.println("Remaining Slots: " + remainingSlots);
            System.out.println("Favorite Zones IDs: " + favoriteZonesIds);
            System.out.println("Page: " + page);
        } else {
            nonFavoriteParkingZones = new ArrayList<>();
        }

        // 3️⃣ 전체 아이템 수 조회 (총 페이지 수 계산을 위해)
        long totalItems = parkingZoneRepository.count(); // 전체 아이템 수를 구하는 쿼리 (COUNT 쿼리)
        long totalPages = (totalItems) / pagePerItems + 1; // 총 페이지 수 계산

        // 4️⃣ 최종 주차장 리스트 만들기
        List<ParkingZoneDTO> parkingZones = new ArrayList<>();

        // 1. 즐겨찾기 주차장 변환 후 추가
        for (ParkingZoneEntity favoriteZone : favoriteZones) {
            // 즐겨찾기 여부를 true로 설정하고, 예약 여부는 기본값으로 설정 (예: false)
            List<ParkingFeeRuleDTO> parkingFeeRules = parkingFeeRuleDTOConverter.toDtoList(parkingFeeRuleRepository.findAllByParkingZoneEntity_Id(favoriteZone.getId())); // 요금 규칙 목록 가져오기
            ParkingZoneDTO favoriteZoneDTO = parkingZoneDTOConverter.toDTO(favoriteZone, true, parkingFeeRules);
            parkingZones.add(favoriteZoneDTO);
        }

        // 2. 일반 주차장 변환 후 추가
        for (ParkingZoneEntity nonFavoriteZone : nonFavoriteParkingZones) {
            // 즐겨찾기 여부는 false, 예약 여부는 기본값으로 설정 (예: false)
            List<ParkingFeeRuleDTO> parkingFeeRules = parkingFeeRuleDTOConverter.toDtoList(parkingFeeRuleRepository.findAllByParkingZoneEntity_Id(nonFavoriteZone.getId())); // 요금 규칙 목록 가져오기
            ParkingZoneDTO nonFavoriteZoneDTO = parkingZoneDTOConverter.toDTO(nonFavoriteZone, false, parkingFeeRules);
            parkingZones.add(nonFavoriteZoneDTO);
        }
        // 5️⃣ 페이지네이션 정보 설정
        ParkingZoneListResponse.PaginationInfo paginationInfo = ParkingZoneListResponse.PaginationInfo.builder()
                .currentPage(page)
                .totalPages(totalPages)
                .pagePerItem((long) pagePerItems )
                .totalItems(totalItems)
                .build();

        // 6️⃣ 응답 객체 생성
        return ParkingZoneListResponse.builder()
                .parkingZones(parkingZones)
                .pagination(paginationInfo)
                .build();
    }
}
