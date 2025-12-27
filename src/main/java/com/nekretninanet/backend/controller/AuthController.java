package com.nekretninanet.backend.controller;

import com.nekretninanet.backend.dto.LoginRequestDto;
import com.nekretninanet.backend.dto.LoginResponseDto;
import com.nekretninanet.backend.dto.RegisterRequestDto;
import com.nekretninanet.backend.model.User;
import com.nekretninanet.backend.security.CustomUserDetailsService;
import com.nekretninanet.backend.security.JwtUtil;
import com.nekretninanet.backend.service.TokenVersionService;
import com.nekretninanet.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AuthController {

    private final UserService userService;
    private final TokenVersionService tokenVersionService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, TokenVersionService tokenVersionService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.tokenVersionService = tokenVersionService;
        this.authenticationManager = authenticationManager;
    }


    @PreAuthorize("isAnonymous()")
    @PostMapping("/auth/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDto dto) {
        userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto, HttpServletResponse response) {

        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                dto.getUsername(), dto.getPassword()));

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        int newTokenVersion = tokenVersionService.incrementVersion(user.getId());
        String token = jwtUtil.generateToken(userDetails, user.getId(), newTokenVersion);

        // Kreiranje HttpOnly
        ResponseCookie jwtCookie = ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(false) // U produkciji obavezno TRUE (za HTTPS)
                .path("/")
                .maxAge(3600) // 1 sat
                .sameSite("Lax")
                .build();

        response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, jwtCookie.toString());

        return ResponseEntity.ok(new LoginResponseDto("token", user.getUsername(), user.getUserType().name()));
    }

    @GetMapping("/auth/me")
    public ResponseEntity<LoginResponseDto> getCurrentUser(Authentication auth, HttpServletRequest request) {
        //CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        //CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        /*if (csrfToken != null) {
            System.out.println("CSRF Token generisan: " + csrfToken.getToken());
        }

         */


        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findByUsername(auth.getName());
        // Token proslijeđen u DTO može biti null jer je u kolačiću
        return ResponseEntity.ok(new LoginResponseDto(null, user.getUsername(), user.getUserType().name()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(Authentication auth, HttpServletResponse response) {
        if (auth != null) {
            User user = userService.findByUsername(auth.getName());
            tokenVersionService.incrementVersion(user.getId());
        }

        // Brisanje kolačića postavljanjem maxAge na 0
        ResponseCookie deleteCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, deleteCookie.toString());
        return ResponseEntity.ok().build();
    }

}
