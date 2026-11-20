package org.example.usersapi.controller;

import jakarta.validation.Valid;
import org.example.usersapi.dto.LoginData;
import org.example.usersapi.dto.UserRequest;
import org.example.usersapi.dto.UserResponse;
import org.example.usersapi.service.AuthService;
import org.example.usersapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/validate")
    public Map<String, Object> validate(@RequestHeader("Authorization") String token){
        return authService.parseTokenToMap(token);
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginData loginData){
        return authService.checkLogin(loginData);
    }

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody UserRequest userRequest){
        return userService.registerUser(userRequest);
    }
}
