package org.example.honorsparkingbe.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.honorsparkingbe.dto.ParkingZoneWithMatchedInfoDTO;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingZoneSearchResponse {

  Meta meta;
  List<ParkingZoneWithMatchedInfoDTO> parkingZones;

  @Data
  @Builder
  public static class Meta {

    String keyword;
    Boolean isEnd;
    PaginationResponse pagination;

  }

}
