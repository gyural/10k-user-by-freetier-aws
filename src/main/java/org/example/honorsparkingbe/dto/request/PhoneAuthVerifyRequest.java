package org.example.honorsparkingbe.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PhoneAuthVerifyRequest {
    // 인증번호가 맞는지 검증 요청하는 DTO
    private String phoneNumber;
    private String authCode;

}

// 인증번호(authCode) 를 Redis에 저장할 때 "phoneauth:{phoneNumber}" 라는 전화번호 기반 키로 저장했기 때문에,
// 검증 시에도 어떤 전화번호 기준으로 인증번호를 꺼낼지 알아야 한다.
// 그러므로 authCode 만이 아닌 phoneNumber도 필요한 것.