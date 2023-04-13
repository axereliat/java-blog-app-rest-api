package com.blogapp.service;

import com.blogapp.payload.LoginDto;
import com.blogapp.payload.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);
}
