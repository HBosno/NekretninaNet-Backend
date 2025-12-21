package com.nekretninanet.backend.controller;

import com.nekretninanet.backend.dto.LoginRequestDto;
import com.nekretninanet.backend.dto.LoginResponseDto;
import com.nekretninanet.backend.dto.RegisterRequestDto;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.security.CustomUserDetailsService;
import com.nekretninanet.backend.security.JwtUtil;
import com.nekretninanet.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(UserService userService, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    @PostMapping("/auth/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDto dto) {
        userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {

        User user = userService.login(dto);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return ResponseEntity
                .ok(new LoginResponseDto(token,
                        user.getUsername(),
                        user.getUserType().name()));
    }

}
