package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.dto.mypage.GetUserInfoResponseDTO;
import org.example.honorsparkingbe.dto.mypage.UpdateUserCarNumberRequestDTO;
import org.example.honorsparkingbe.dto.mypage.UpdateUserInfoRequestDTO;
import org.example.honorsparkingbe.repository.internal.CarRepository;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.example.honorsparkingbe.security.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MyPageService {

    private final MemberRepository memberRepository;
    private final CarRepository carRepository;

    public MyPageService(MemberRepository memberRepository, CarRepository carRepository) {
        this.memberRepository = memberRepository;
        this.carRepository = carRepository;
    }

    public GetUserInfoResponseDTO getUserInfo() {
        Long userId= SecurityUtil.getCurrentUserId();

        MemberEntity user= memberRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("해당 사용자 정보가 DB에 없음"));

        return new GetUserInfoResponseDTO( // 생성자 활용
                user.getUserName(),
                user.getAuthId(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getBirthdayYear(),
                user.getBirthday(),
                user.getLoginPlatform(),
                user.getCarEntity() != null ? user.getCarEntity().getCarNumber() : null
        );
    }

    public void updateUserInfo(UpdateUserInfoRequestDTO dto) {
        Long userId= SecurityUtil.getCurrentUserId();

        MemberEntity user= memberRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("해당 사용자 정보가 DB에 없음"));

        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        memberRepository.save(user);
    }

    public void updateUserCarNumber(UpdateUserCarNumberRequestDTO request) {
        Long userId= SecurityUtil.getCurrentUserId();

        MemberEntity user= memberRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("해당 사용자 정보가 DB에 없음"));

        CarEntity car= user.getCarEntity();
        if (car == null) {
            throw new IllegalStateException("아직 등록된 차량 없음");
        }

        // 30일 확인
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastUpdate = car.getCreatedAt();

        if(lastUpdate!=null && lastUpdate.isAfter(now.minusDays(30))) {
            throw new IllegalArgumentException("차량 번호는 30일에 한 번 변경 가능");
        }

        car.setCreatedAt(now);
        car.setCarNumber(request.getCarNumber());
        carRepository.save(car);
    }
}
