package org.example.honorsparkingbe.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GetParkingZoneByKeywordDTO {

  private String keyword;
}
