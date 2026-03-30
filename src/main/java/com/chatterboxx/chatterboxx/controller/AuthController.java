package com.chatterboxx.chatterboxx.controller;

import com.chatterboxx.chatterboxx.config.JwtUtil;
import com.chatterboxx.chatterboxx.dto.AuthResponse;
import com.chatterboxx.chatterboxx.dto.LoginRequest;
import com.chatterboxx.chatterboxx.dto.RegisterRequest;
import com.chatterboxx.chatterboxx.entities.User;
import com.chatterboxx.chatterboxx.repositories.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepo userRepo,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        String username = request.getUsername().trim();
        String password = request.getPassword().trim();

        if (username.isEmpty() || password.isEmpty()) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }

        if (password.length() < 6) {
            return ResponseEntity.badRequest().body("Password must be at least 6 characters");
        }

        if (userRepo.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepo.save(user);

        String token = jwtUtil.generateToken(username);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, username));
    }

    // ✅ Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        String username = request.getUsername().trim();
        String password = request.getPassword().trim();

        User user = userRepo.findByUsername(username)
                .orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        String token = jwtUtil.generateToken(username);
        return ResponseEntity.ok(new AuthResponse(token, username));
    }

    // ✅ Validate token (used by frontend on refresh)
    @GetMapping("/validate")
    public ResponseEntity<?> validate() {
        // If this endpoint is reached, JwtFilter already validated the token
        return ResponseEntity.ok("Token is valid");
    }
}