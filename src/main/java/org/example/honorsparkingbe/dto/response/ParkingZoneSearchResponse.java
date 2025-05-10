package org.example.honorsparkingbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingZoneSearchResponse {

  Meta meta;
  PaginationResponse paginationResponse;

  public static class Meta {
    
  }
}
