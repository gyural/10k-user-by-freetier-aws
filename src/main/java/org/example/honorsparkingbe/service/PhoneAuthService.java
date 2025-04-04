package org.example.honorsparkingbe.service;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.example.honorsparkingbe.util.CoolSmsUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PhoneAuthService {

    private final StringRedisTemplate redisTemplate;
    private final CoolSmsUtil coolSmsUtil;
    private final MemberRepository memberRepository;

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

        // 중복 인증 요청 방지
//        String verifiedKey = "verified:" + phoneNumber;
//        if (Boolean.TRUE.equals(redisTemplate.hasKey(verifiedKey))) {
//            throw new IllegalStateException("이미 인증을 완료한 번호입니다.");
//        }

        // 이미 가입된 전화번호인지 확인
        if (memberRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalStateException("이미 가입된 전화번호입니다.");
        }


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

            // 인증 완료된 전화번호로 상태 기록
//            String verifiedKey = "verified:" + phoneNumber;
//            redisTemplate.opsForValue().set(verifiedKey, "true", Duration.ofMinutes(1));  // 인증 완료 상태 저장

            return true;
        }

        return false;
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
