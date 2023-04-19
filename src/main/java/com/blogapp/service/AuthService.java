package com.blogapp.service;

import com.blogapp.payload.*;

public interface AuthService {
    AuthDto login(LoginDto loginDto);

    String register(RegisterDto registerDto);

    UserDto getProfileInformation(Long id);

    UserDto editProfile(ProfileDto profileDto);
}
