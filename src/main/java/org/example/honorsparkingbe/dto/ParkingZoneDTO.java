package org.example.honorsparkingbe.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ParkingZoneDTO {
    private boolean isFavorite;
    private double latitude;
    private double longitude;
    private String zoneName;
    private String cityName;
    private String districtName;
    private String eupMyeonDongName;
    private int electricCarSpaceCount;
    private boolean isReservedOk;
    private int size;
    private List<Integer> floor;
    private int maxCost;
    private int hourlyRate;
    private int minuteRate;
}
