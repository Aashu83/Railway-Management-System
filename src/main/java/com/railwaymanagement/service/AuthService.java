package com.railwaymanagement.service;

import com.railwaymanagement.dto.AuthResponse;
import com.railwaymanagement.dto.LoginRequest;
import com.railwaymanagement.dto.RegisterRequest;
import com.railwaymanagement.entity.User;
import com.railwaymanagement.enums.Role;
import com.railwaymanagement.exception.DuplicateEmailException;
import com.railwaymanagement.repository.UserRepository;
import com.railwaymanagement.security.JwtService;
import com.railwaymanagement.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // self-registration is always plain USER;
                // ADMIN/STAFF accounts are provisioned separately, not
                // chosen by the caller — never trust a role in the request body.
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(new UserPrincipal(user));

        return AuthResponse.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Delegates to the configured AuthenticationProvider (DaoAuthenticationProvider),
        // which uses UserDetailsServiceImpl + PasswordEncoder under the hood.
        // Throws BadCredentialsException on mismatch — handled globally.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(); // unreachable if authenticate() above succeeded

        String token = jwtService.generateToken(new UserPrincipal(user));

        return AuthResponse.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
