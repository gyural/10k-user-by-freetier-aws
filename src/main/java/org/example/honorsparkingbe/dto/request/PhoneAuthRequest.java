package org.example.honorsparkingbe.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PhoneAuthRequest {
    // 전화 번호로 인증번호 문자 전송 요청용 DTO

    // 쿨 sms 에서는 반드시 하이픈(-) 없이 숫자만으로 구성된 형식 지켜야 함
    // ex) 01012345678
    private String phoneNumber;

}
