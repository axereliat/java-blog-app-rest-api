package com.blogapp.service;

import com.blogapp.payload.AuthDto;
import com.blogapp.payload.LoginDto;
import com.blogapp.payload.RegisterDto;
import com.blogapp.payload.UserDto;

public interface AuthService {
    AuthDto login(LoginDto loginDto);

    String register(RegisterDto registerDto);

    UserDto getProfileInformation(Long id);
}
