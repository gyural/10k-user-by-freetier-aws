package org.example.honorsparkingbe.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParkingZoneWithMatchedInfoDTO {

  private List<MatchedInfoElement> matchedInfo;
  private Boolean isFavorite;
  private Double latitude;
  private Double longitude;
  private String zoneName;
  private String cityName;
  private String districtName;
  private String eupMyeonDongName;
  private Integer electricCarSpaceCount;
  private Integer size;
  private Integer maxCost;
  private List<ParkingFeeRuleDTO> parkingFeeRules;  // 요금 정보
  private String thumbnail;


  @Data
  @Builder
  public static class MatchedInfoElement {

    private String field;
    private String value;
    private String matchedText;
    private Integer startIndex;
    private Integer endIndex;
  }
}
