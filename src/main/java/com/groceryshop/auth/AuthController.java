package com.groceryshop.auth;

import com.groceryshop.shared.dto.request.LoginRequest;
import com.groceryshop.shared.dto.request.RegisterRequest;
import com.groceryshop.shared.dto.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        AuthResponse response = new AuthResponse(
            null, // No token for registration response
            null, // No refresh token for registration response
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logout current user")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        authService.logout(jwtToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate JWT token")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current authenticated user information")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<User> getCurrentUser() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(user);
    }
}
