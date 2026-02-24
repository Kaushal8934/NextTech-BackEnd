package com.nexttech.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDto {
    private Long user_id;
    private String user_name;
    private String email;
    private String first_name;
    private String last_name;
    private Long mobile_number;
    private String profile_pic;
    private boolean email_verified;
    private boolean phone_verified;
}
