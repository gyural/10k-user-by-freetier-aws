package org.example.honorsparkingbe.util;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Component;

// 실제 쿨 sms를 호출해서 문자를 보내주는 유틸 클래스

@Component
public class CoolSmsUtil {

    public void sendSms(String apiKey, String apiSecret, String from, String to, String text) {

        // 쿨 sms 서버와 통신할 수 있는 객체 생성
        // SDK 초기화 -> 인증을 위한 apiKey, apiSecret, 문자 전송용 api 서버 주소
        DefaultMessageService messageService =
                NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");

        // 쿨 sms 서버로 연결 준비 완료

        Message message = new Message(); // 객체 생성
        message.setFrom(from); // 발신 번호
        message.setTo(to); // 수신 번호
        message.setText(text); // 문자 내용

        messageService.sendOne(new SingleMessageSendingRequest(message));
    }

}
