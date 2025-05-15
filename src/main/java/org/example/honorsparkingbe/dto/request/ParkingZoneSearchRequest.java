package org.example.honorsparkingbe.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ParkingZoneSearchRequest {

  @NotNull(message = "keyword 쿼리 파라매터는 필수입니다.")
  private String keyword;
  private Integer page;
}
