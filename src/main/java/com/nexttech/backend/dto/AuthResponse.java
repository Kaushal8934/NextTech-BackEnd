package com.nexttech.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class  AuthResponse {
    private String username;
    private String email;
    private String token;
    private String firstName;
    private String lastName;
    private Long mobileNumber;
}
