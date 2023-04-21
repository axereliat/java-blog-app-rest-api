package com.blogapp.service.impl;

import com.blogapp.entity.Role;
import com.blogapp.entity.User;
import com.blogapp.exception.BlogAPIException;
import com.blogapp.exception.ResourceNotFoundException;
import com.blogapp.payload.*;
import com.blogapp.repository.RoleRepository;
import com.blogapp.repository.UserRepository;
import com.blogapp.security.JwtTokenProvider;
import com.blogapp.service.AuthService;
import com.blogapp.service.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.blogapp.utils.AppConstants.ROLE_ADMIN;
import static com.blogapp.utils.AppConstants.ROLE_USER;

@Service
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;

    private final CloudinaryService cloudinaryService;


    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider, CloudinaryService cloudinaryService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public AuthDto login(LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        Optional<User> temp = this.userRepository.findByEmail(loginDto.getUsernameOrEmail());
        if (temp.isEmpty()) {
            temp = this.userRepository.findByUsername(loginDto.getUsernameOrEmail());
        }
        User user = temp.get();

        return new AuthDto(token, user.getId().toString(), user.getUsername());
    }

    @Override
    public String register(RegisterDto registerDto) {

        // add check for username exists in database
        if(userRepository.existsByUsername(registerDto.getUsername())){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Username is already exists!.");
        }

        // add check for email exists in database
        if(userRepository.existsByEmail(registerDto.getEmail())){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Email is already exists!.");
        }

        User user = new User();
        user.setName(registerDto.getName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole;
        if (this.userRepository.count() > 0) {
            userRole = roleRepository.findByName(ROLE_USER).get();
        } else {
            userRole = roleRepository.findByName(ROLE_ADMIN).get();
        }
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        return "User registered successfully!.";
    }

    @Override
    public UserDto getProfileInformation(Long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", id));

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Override
    public UserDto editProfile(MultipartFile avatar, ProfileDto profileDto) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = this.userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new UsernameNotFoundException("Username could not be found"));

        if (!profileDto.getName().isEmpty()) {
            user.setName(profileDto.getName());
        }

        if (avatar != null) {
            String avatarUrl = this.cloudinaryService.uploadImage(avatar);
            user.setAvatarUrl(avatarUrl);
        }

        this.userRepository.save(user);

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
