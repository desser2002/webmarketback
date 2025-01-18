package com.example.webmarket.controller;

import com.example.webmarket.DTO.JwtResponse;
import com.example.webmarket.DTO.UserDataRequest;
import com.example.webmarket.model.User;
import com.example.webmarket.security.JwtTokenProvider;
import com.example.webmarket.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDataRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String token = jwtTokenProvider.generateToken(authentication);

            User user = userService.findByUsername(request.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Возвращаем токен, ID пользователя и роль
            return ResponseEntity.ok(new JwtResponse(token, user.getId(), user.getRole()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid username or password");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDataRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken.");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole() != null && request.getRole().equalsIgnoreCase("SELLER") ? "SELLER" : "USER");
        userService.saveUser(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PutMapping("/change-role/{username}")
    public ResponseEntity<?> changeUserRoleToAdmin(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        user.setRole("ADMIN");
        userService.saveUser(user);
        return ResponseEntity.ok("User role updated to ADMIN successfully!");
    }

    @GetMapping("/role/{username}")
    public ResponseEntity<?> getUserRole(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok("User role: " + user.getRole());
    }
}
