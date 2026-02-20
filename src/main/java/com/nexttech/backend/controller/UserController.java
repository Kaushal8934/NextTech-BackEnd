package com.nexttech.backend.controller;

import com.nexttech.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUser")
    public ResponseEntity<?> getUserData(@RequestParam Long userId){
        return ResponseEntity.ok(userService.getUserById(userId));
    }

}
