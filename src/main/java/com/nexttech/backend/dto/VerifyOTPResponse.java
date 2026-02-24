package com.nexttech.backend.dto;

import com.nexttech.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOTPResponse {
    private String email;
    private boolean isEmailVerified;
    private boolean userAlreadyExists;
}
