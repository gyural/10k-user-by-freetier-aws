package org.example.honorsparkingbe.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserInfoRequestDTO {
    private String phoneNumber;
    private String email;
}
