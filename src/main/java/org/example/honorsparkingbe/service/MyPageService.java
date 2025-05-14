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
