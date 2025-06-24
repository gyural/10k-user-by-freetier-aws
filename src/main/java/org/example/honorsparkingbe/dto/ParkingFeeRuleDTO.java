package org.example.honorsparkingbe.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingFeeRuleDTO implements Serializable {

  private String ruleName;
  private Integer startTime;
  private Integer endTime;
  private Integer costPerTimeSlot;
  private Integer costTimeSlot;
}
