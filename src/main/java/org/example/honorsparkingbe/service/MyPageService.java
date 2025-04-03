package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.dto.mypage.GetUserInfoResponseDTO;
import org.example.honorsparkingbe.dto.mypage.UpdateUserInfoRequestDTO;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.example.honorsparkingbe.security.util.SecurityUtil;
import org.springframework.stereotype.Service;

@Service
public class MyPageService {

    private final MemberRepository memberRepository;

    public MyPageService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
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
}
