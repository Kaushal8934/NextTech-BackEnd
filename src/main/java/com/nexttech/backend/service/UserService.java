package com.nexttech.backend.service;

import com.nexttech.backend.dto.UserDetailsDto;
import com.nexttech.backend.model.User;
import com.nexttech.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDetailsDto getUserByUserName(String userName) {

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userName));

        return UserDetailsDto.builder()
                .user_id(user.getId())
                .email(user.getEmail())
                .mobile_number(user.getMobileNumber())
                .user_name(user.getUsername())
                .first_name(user.getFirstName())
                .last_name(user.getLastName())
                .email_verified(user.isEmailVerified())
                .phone_verified(user.isPhoneVerified())
                .profile_pic(user.getProfilePic())
                .build();
    }
}