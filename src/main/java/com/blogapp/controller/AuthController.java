package com.blogapp.controller;

import com.blogapp.payload.*;
import com.blogapp.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<JWTAuthResponse> login(@RequestBody LoginDto loginDto){
        AuthDto authDto = authService.login(loginDto);

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(authDto.getToken());
        jwtAuthResponse.setUserId(authDto.getUserId());
        jwtAuthResponse.setUsername(authDto.getUsername());

        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        String response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserDto> profile(@PathVariable Long id) {
        UserDto userDto = this.authService.getProfileInformation(id);

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> profileEdit(@RequestParam(required = false) MultipartFile avatar) throws IOException {
        UserDto userDto = this.authService.editProfile(avatar, new ProfileDto(""));

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
