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
