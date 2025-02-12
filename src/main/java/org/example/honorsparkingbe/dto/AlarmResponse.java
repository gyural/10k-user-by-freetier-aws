package org.example.honorsparkingbe.dto;

import lombok.Getter;
import org.example.honorsparkingbe.domain.entity.AlarmEntity;
import org.example.honorsparkingbe.domain.enums.AlarmType;
import org.example.honorsparkingbe.domain.enums.IsRead;

import java.time.LocalDateTime;

@Getter
public class AlarmResponse {
    private Long id;
    private String content;
    private IsRead isRead;
    private AlarmType alarmType;
    private LocalDateTime createdAt;
    // private LocalDateTime readAt;

    public AlarmResponse(AlarmEntity alarm) {
        this.id = alarm.getId();
        this.content = alarm.getContent();
        this.isRead = alarm.getIsRead();
        this.alarmType = alarm.getAlarmType();
        this.createdAt = alarm.getCreatedAt();
        // this.readAt = alarm.getReadAt();
    }
}
