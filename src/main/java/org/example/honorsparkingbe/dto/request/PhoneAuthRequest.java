package org.example.honorsparkingbe.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PhoneAuthRequest {
    // 전화 번호로 인증번호 문자 전송 요청용 DTO
    private String phoneNumber;

}
