package com.nc.user;

import com.nc.exception.DuplicateException;
import com.nc.exception.UnauthorizedException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public User signup(RegisterUserDTO input) {
        if (userRepository.existsByUsername(input.getUsername())) {
            throw new DuplicateException("Username already exists");
        }

        if (userRepository.existsByEmail(input.getEmail())) {
            throw new DuplicateException("Email already exists");
        }

        User user = new User();
        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDTO input) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword()));

        return userRepository
                .findByUsername(input.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
    }
}