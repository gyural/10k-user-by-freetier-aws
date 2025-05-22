package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.dto.mypage.ChangeUserPasswordRequestDTO;
import org.example.honorsparkingbe.dto.mypage.GetUserInfoResponseDTO;
import org.example.honorsparkingbe.dto.mypage.UpdateUserCarNumberRequestDTO;
import org.example.honorsparkingbe.dto.mypage.UpdateUserInfoRequestDTO;
import org.example.honorsparkingbe.repository.internal.CarRepository;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.example.honorsparkingbe.security.util.SecurityUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class MyPageService {

    private final MemberRepository memberRepository;
    private final CarRepository carRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MyPageService(MemberRepository memberRepository, CarRepository carRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.carRepository = carRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
        LocalDateTime now = LocalDateTime.now();

        if (car == null) {
            CarEntity newCar = CarEntity.builder()
                    .carNumber(request.getCarNumber())
                    .createdAt(now)
                    .build();

            carRepository.save(newCar);

            // 연관관계 설정
            user.setCarEntity(newCar);
            memberRepository.save(user); // 사용자에 차량 정보 반영
        }else{
            // 차량이 있는 경우: 30일 내 수정 제한
            LocalDateTime lastUpdate = car.getCreatedAt();

            if (lastUpdate != null && lastUpdate.isAfter(now.minusDays(30))) {
                throw new IllegalArgumentException("차량 번호는 30일에 한 번 변경 가능");
            }

            car.setCarNumber(request.getCarNumber());
            car.setCreatedAt(now); // 갱신 시간
            carRepository.save(car);
        }


    }

    public boolean checkUserPassword(String currentPassword) {
        Long userId = SecurityUtil.getCurrentUserId();
        MemberEntity user = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자 정보가 DB에 없음"));

        // 소셜 로그인 계정은 비밀번호가 없을 수 있음
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalStateException("비밀번호 확인은 일반 로그인 사용자만 가능합니다.");
        }

        return bCryptPasswordEncoder.matches(currentPassword, user.getPassword());
    }


    public void changeUserPassword(ChangeUserPasswordRequestDTO request) {
        Long userId= SecurityUtil.getCurrentUserId();

        MemberEntity user= memberRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("해당 사용자 정보가 DB에 없음"));

        // 일반로그인 계정인지 확인해야 함
        Boolean flag= SecurityUtil.getCurrentUserLoginPlatform();
        if(flag==false) {
            throw new IllegalStateException("소셜 로그인 계정은 비밀번호 변경 불가");
        }

        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        memberRepository.save(user);
    }

    public void updateUserRoleToUser() {
        Long userId = SecurityUtil.getCurrentUserId();

        MemberEntity user = memberRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자 정보가 DB에 없음"));

        user.setRole(MemberRole.ROLE_USER);
        memberRepository.save(user);
    }
}
