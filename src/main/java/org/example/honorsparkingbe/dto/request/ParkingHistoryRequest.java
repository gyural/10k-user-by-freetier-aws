package org.example.honorsparkingbe.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ParkingHistoryRequest {
    private int page = 1;
    private int number = 10;
    private String startTime;
    private String endTime;
}
