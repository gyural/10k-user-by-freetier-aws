package org.example.honorsparkingbe.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinDTO {
    private String platform;          // Login platform
    private String mobile;            // Phone number
    private String name;              // User name
    private String birthyear;         // Birth year
    private String birthday;          // Birth day
    private String carNumber;         // Car number
    private String accountId;         // Account ID
    private String accountPassword;   // Account password
    private String email;             // Email
}
