package org.example.honorsparkingbe.service;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.example.honorsparkingbe.util.CoolSmsUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PhoneAuthService {


    private final StringRedisTemplate redisTemplate;
    private final MemberRepository memberRepository;
    private final CoolSmsUtil coolSmsUtil;

    @Value("${coolsms.from}")
    private String from;

    @Value("${coolsms.apiKey}")
    private String apiKey;

    @Value("${coolsms.apiSecret}")
    private String apiSecret;

    /**
     * 인증번호 생성 및 문자 전송
     * POST /api/v1/phone-auth/send
     * @param phoneNumber
     */
    public void sendAuthCode(String phoneNumber) {
        String authCode = generateCode();
        saveAuthCodeToRedis(phoneNumber, authCode);

        String message = "[HonorsParking] 인증번호는 " + authCode + "입니다.";
        coolSmsUtil.sendSms(apiKey, apiSecret, from, phoneNumber, message);
    }


    /**
     * 인증번호 검증 및 사용자 정보 반영
     * POST /api/v1/phone-auth/verify
     * @param phoneNumber
     * @param inputCode
     * @return
     */
    public boolean verifyAuthCode(String phoneNumber, String inputCode) {
        String key = "phoneauth:" + phoneNumber;
        String savedCode = redisTemplate.opsForValue().get(key);

        if (savedCode != null && savedCode.equals(inputCode)) {

            // 인증 성공 시, Redis에서 인증번호 삭제
            redisTemplate.delete(key);

            Long memberId = getCurrentMemberId();  // 현재 로그인한 사용자 ID 가져오기
            MemberEntity member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalStateException("회원 정보 없음"));

            member.setPhoneNumber(phoneNumber);
            memberRepository.save(member);

            return true;
        }

        return false;
    }

    // 전화번호 인증 기능은 "로그인한 사용자의 전화번호를 저장하는 것" 자체가 목적이기 때문에
    // 서비스 코드에서 SecurityContextHolder 사용하는 방식이 더 자연스럽고 실무에서 흔하다고 함. (Chat GPT)

    /**
     * 현재 로그인한 사용자의 memberId 가져오기
     * 일반 로그인: UserDetails에서 authId 가져와서 memberId 조회
     * 소셜 로그인: OAuth2User에서 socialId 가져와서 memberId 조회
     * @return
     */
    private Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return findMemberIdByAuthId(userDetails.getUsername());
        }

        if (principal instanceof OAuth2User oAuth2User) {
            String authId = (String) oAuth2User.getAttribute("sub");
            return findMemberIdByAuthId(authId);
        }

        throw new IllegalStateException("Invalid user authentication data");
    }

    /**
     * 로그인 사용자의 authId로 memberId 조회
     */
    private Long findMemberIdByAuthId(String authId) {
        return memberRepository.findByAuthId(authId).getId();
    }

    /**
     * 6자리 랜덤 인증번호 생성
     * @return
     */
    private String generateCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    /**
     * 인증번호를 Redis에 저장
     * key = phoneauth: {전화번호}
     * 만료 시간 = 3분
     * @param phoneNumber
     * @param code
     */
    private void saveAuthCodeToRedis(String phoneNumber, String code) {
        String key = "phoneauth:" + phoneNumber;
        redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(3)); // TTL: 3분
    }

}
