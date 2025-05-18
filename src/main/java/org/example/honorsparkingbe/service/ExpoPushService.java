package org.example.honorsparkingbe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Expo 서버에 HTTP POST 요청을 보내는 역할만 수행
 */

@Service
@RequiredArgsConstructor
public class ExpoPushService {

    private final RestTemplate restTemplate;

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    public void sendPushNotification(String pushToken, String title, String body, Map<String, Object> data) {
        Map<String, Object> message = new HashMap<>();
        message.put("to", pushToken);
        message.put("title", title);
        message.put("body", body);
        message.put("sound", "default");
        message.put("priority", "high");
        message.put("data", data);

        List<Map<String, Object>> messageList = List.of(message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<Map<String, Object>>> request = new HttpEntity<>(messageList, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_URL, request, String.class);
            System.out.println("Expo 응답: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Expo 푸시 실패: " + e.getMessage());
        }
    }
}