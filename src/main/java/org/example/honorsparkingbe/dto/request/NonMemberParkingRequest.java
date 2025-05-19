package org.example.honorsparkingbe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NonMemberParkingRequest {

    @NotBlank(message = "차량 번호는 필수입니다.")
    private String vehicleNumber;
}
//3