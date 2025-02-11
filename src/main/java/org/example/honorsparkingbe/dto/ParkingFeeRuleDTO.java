package org.example.honorsparkingbe.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ParkingFeeRuleDTO {
    private String ruleName;
    private Integer startTime;
    private Integer endTime;
    private Integer costPerTimeSlot;
    private Integer costTimeSlot;
}
