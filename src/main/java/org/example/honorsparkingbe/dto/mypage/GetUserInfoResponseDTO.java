package org.example.honorsparkingbe.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.honorsparkingbe.domain.enums.LoginPlatform;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserInfoResponseDTO {
    private String userName;
    private String authId;
    private String phoneNumber;
    private String email;
    private int birthdayYear;
    private String birthday;
    private LoginPlatform loginPlatform;
    private String carNumber;
}
