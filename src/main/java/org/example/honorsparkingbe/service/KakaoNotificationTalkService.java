//package org.example.honorsparkingbe.service;
//
//import java.io.IOException;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Optional;
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import org.apache.commons.net.util.Base64;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//@Component
//
//public class KakaoNotificationTalkService {
//
//    private final String serviceID;
//    private final String ncpAccessKey;
//    private final String ncpSecretKey;
//    private final String plusFriendId;
//    private final String templateCodeCarEntry;
//    private final String templateCodeCarExit;
//
//
//  public KakaoNotificationTalkService(
//        @Value("${NCP_SERVICE_ID}") String serviceID,
//        @Value("${NCP_ACCESS_KEY}") String ncpAccessKey,
//        @Value("${NCP_SECRET_KEY}") String ncpSecretKey,
//        @Value("${NCP_PLUS_FRIEND_ID}") String plusFriendId,
//        @Value("${NCP_TEMPLATE_CODE_CAR_ENTRY}") String templateCodeCarEntry,
//        @Value("${NCP_TEMPLATE_CODE_CAR_EXIT}") String templateCodeCarExit
//        ) {
//      this.serviceID = serviceID;
//      this.ncpAccessKey = ncpAccessKey;
//      this.ncpSecretKey = ncpSecretKey;
//      this.plusFriendId = plusFriendId;
//      this.templateCodeCarEntry = templateCodeCarEntry;
//      this.templateCodeCarExit = templateCodeCarExit;
//    }
//
//  /**
//   *
//   * @param to
//   * @param templateCode
//   * @param content
//   * @param buttons
//   * @param isReservedMessage
//   * @param targetDateTime
//   */
//    public void sendAlimTalk(String to,
//      String templateCode,
//      String content,
//      JSONArray buttons,
//      boolean isReservedMessage,
//      String targetDateTime
//  ) {
//    String alimTalkSendRequestUrl =
//        "https://sens.apigw.ntruss.com/alimtalk/v2/services/" + serviceID + "/messages";
//    String alimTalkSignatureRequestUrl = "/alimtalk/v2/services/" + serviceID + "/messages";
//    CloseableHttpClient httpClient = null;
//
//    try {
//      String[] signatureArray = makePostSignature(ncpAccessKey, ncpSecretKey,
//          alimTalkSignatureRequestUrl);
//
//      // http 통신 객체 생성
//      httpClient = HttpClients.createDefault(); // http client 생성
//      HttpPost httpPost = new HttpPost(alimTalkSendRequestUrl); // post 메서드와 URL 설정
//
//      // 헤더 설정
//      httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
//      httpPost.setHeader("x-ncp-iam-access-key", ncpAccessKey);
//      httpPost.setHeader("x-ncp-apigw-timestamp", signatureArray[0]);
//      httpPost.setHeader("x-ncp-apigw-signature-v2", signatureArray[1]);
//
//      // 메시지 객체 구성
//      JSONObject msgObj = new JSONObject();
//      msgObj.put("plusFriendId", plusFriendId);
//      msgObj.put("templateCode", templateCode);
//
//      // 메시지 내용 구성
//      JSONObject messages = new JSONObject();
//      messages.put("countryCode", "82");  // 국가 코드
//      messages.put("to", to); // 전화번호
//      messages.put("content", content);  // 메시지 내용
//
//      //reserved Time 필드 등록
//      if (isReservedMessage) {
//        msgObj.put("reserveTime", targetDateTime);
//      }
//
//      // 버튼 추가
//      if (buttons != null) {
//        messages.put("buttons", buttons);
//      }
//
//      // 메시지 객체 배열에 메시지 추가
//      JSONArray messageArray = new JSONArray();
//      messageArray.put(messages);
//
//      // 메시지 배열을 메시지 객체에 포함
//      msgObj.put("messages", messageArray);
//
//      // API 전송 값 http 객체에 담기
//      httpPost.setEntity(new StringEntity(msgObj.toString(), ContentType.APPLICATION_JSON));
//
//      // API 호출
//      CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
//
//      // 응답 결과
//      String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
//      System.out.println(result);
//
//    } catch (Exception ex) {
//      ex.printStackTrace();
//    } finally {
//      try {
//        if (httpClient != null) {
//          httpClient.close();
//        }
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }
//  }
//  public void sendcarEntryAlarm(String phoneNumber, String carNumber, LocalDateTime entranceTime) {
//
//    if (entranceTime == null) {
//      System.out.println("오류: 입차 시간이 없습니다.");
//      return;
//    }
//
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd / HH:mm:ss");
//    String entryTimeString = entranceTime.format(formatter);
//
//    JSONArray buttons = new JSONArray();
//
//    JSONObject button1 = new JSONObject();
//    button1.put("name", "👉 카드 등록하기");
//    button1.put("type", "AL");
//    button1.put("schemeAndroid", "https://honorsparking-web.vercel.app/");
//    button1.put("schemeIos", "https://honorsparking-web.vercel.app/");
//    buttons.put(button1);
//
//    JSONObject button2 = new JSONObject();
//    button2.put("name", "📍 주차 정보 확인");
//    button2.put("type", "AL");
//    button2.put("schemeAndroid", "https://honorsparking-web.vercel.app/");
//    button2.put("schemeIos", "https://honorsparking-web.vercel.app/");
//    buttons.put(button2);
//
//    String content = String.format(
//        "🚗차량 입차 알림🚗\n\n" +
//            "안녕하세요! 고객님, 차량이 주차장에 입차되었습니다.\n\n" +
//            "입차 정보\n" +
//            "• 차량번호: %s\n" +
//            "• 입차 시간: %s\n\n" +
//            "💡편리한 주차를 위한 꿀팁!\n" +
//            "출차할 때 결제 기다리는 시간, 아깝지 않으셨나요?\n\n" +
//            "💳 지금 바로 카드 등록하고, 빠르고 편리하게 출차하세요!\n",
//        carNumber, entryTimeString
//    );
//
//    sendAlimTalk(
//        phoneNumber,
//        templateCodeCarEntry,
//        content,
//        buttons,
//        false,
//        null
//    );
//  }
//
//
//  public void sendCarExitAlarm(String Id, LocalDateTime entranceTime) {
//
//    if (entranceTime == null) {
//      System.out.println("오류: 입차 시간이 없습니다.");
//      return;
//    }
//
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd / HH:mm:ss");
//    // 버튼 추가
//    JSONArray buttons = new JSONArray();
//
//    // 이용 내역 확인 버튼
//    JSONObject button1 = new JSONObject();
//    button1.put("name", "👉 이용 내역 확인");
//    button1.put("type", "AL");
//    button1.put("schemeAndroid", "https://honorsparking-web.vercel.app/"); // ✅ 직접 추가
//    button1.put("schemeIos", "https://honorsparking-web.vercel.app/"); // ✅ 직접 추가
//    buttons.put(button1);
//
//    // 알림톡 메시지 내용 구성
//    String content = String.format(
//
//        "안녕하세요, 고객님! 차량이 출차되었습니다!\n\n"+
//        "📝 이용 내역이 궁금하다면?\n"+
//        "아래 버튼을 눌러 바로 확인하세요!\n"
//    );
//
//    // 메시지 전송 로직 (sendAlimTalk 호출)
//    sendAlimTalk(Id, templateCodeCarExit, content, buttons, false, null);
//  }
//
//  public String[] makePostSignature(String accessKey, String secretKey, String url) {
//      String[] result = new String[2];
//      try {
//        String timeStamp = String.valueOf(
//            Instant.now().toEpochMilli()); // current timestamp (epoch)
//        String space = " "; // space
//        String newLine = "\n"; // new line
//        String method = "POST"; // method
//        String message =
//            new StringBuilder()
//                .append(method)
//                .append(space)
//                .append(url)
//                .append(newLine)
//                .append(timeStamp)
//                .append(newLine)
//                .append(accessKey)
//                .toString();
//
//        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
//        Mac mac = Mac.getInstance("HmacSHA256");
//        mac.init(signingKey);
//
//        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
//        String encodeBase64String = Base64.encodeBase64String(rawHmac);
//
//        result[0] = timeStamp;
//        result[1] = encodeBase64String;
//
//      } catch (Exception ex) {
//        ex.printStackTrace();
//      }
//      return result;
//    }
//  }