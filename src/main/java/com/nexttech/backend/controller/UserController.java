package com.nexttech.backend.controller;

import com.nexttech.backend.dto.UserDetailsDto;
import com.nexttech.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getCurrentUser")
    public ResponseEntity<UserDetailsDto> getUserData(@AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();
        return ResponseEntity.ok(userService.getUserByUserName(username));
    }

}
