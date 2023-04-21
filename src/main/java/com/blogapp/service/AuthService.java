package com.blogapp.service;

import com.blogapp.payload.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AuthService {
    AuthDto login(LoginDto loginDto);

    String register(RegisterDto registerDto);

    UserDto getProfileInformation(Long id);

    UserDto editProfile(MultipartFile avatar, ProfileDto profileDto) throws IOException;
}
