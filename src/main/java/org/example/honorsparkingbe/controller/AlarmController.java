package org.example.honorsparkingbe.controller;

import org.example.honorsparkingbe.service.AlarmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AlarmController {

    private final AlarmService alarmService;

    public AlarmController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    /**
     * 회원 알람 불러오기
     * GET /api/v1/alarmAll
     * @param memberid
     * @param category
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/alarmAll")
    public ResponseEntity<Map<String, Object>> getAlarms(
            @RequestParam Long memberid,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            // JPA 에서는 page 0부터 시작하므로
            int adjustedPage = Math.max(page - 1, 0); // 음수 방지
            Map<String, Object> response = alarmService.getAlarms(memberid, category, adjustedPage, size);

            // Pagination 값 조정
            Map<String, Object> pagination = (Map<String, Object>) response.get("pagination");
            pagination.put("currentPage", (int) pagination.get("currentPage") + 1);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 회원 알람 읽기
     * PUT /api/v1/alarm
     * @param requestBody
     * @return
     */
    @PutMapping("/alarm")
    public ResponseEntity<Map<String, Object>> updateAlarmsToRead(@RequestBody Map<String, List<Long>> requestBody) {
        // 요청에서 alarmIDList 추출
        List<Long> alarmIDList = requestBody.get("alarmIDList");

        try {
            // 서비스 호출
            Map<String, Object> response = alarmService.updateAlarmsToRead(alarmIDList);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
