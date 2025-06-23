package pl.wojtek.project.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.wojtek.project.message.request.LoginRequest;
import pl.wojtek.project.message.request.RegisterRequest;
import pl.wojtek.project.message.response.JwtResponse;
import pl.wojtek.project.message.response.ResponseMessage;
import pl.wojtek.project.model.User;
import pl.wojtek.project.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.login(loginRequest);
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return new ResponseEntity<>(new ResponseMessage("User registered successfully."), HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUserDetails(Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = authService.getUserByUsername(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}