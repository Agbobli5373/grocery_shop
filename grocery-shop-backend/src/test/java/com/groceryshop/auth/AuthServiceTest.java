package com.groceryshop.auth;

import com.groceryshop.TestDataFactory;
import com.groceryshop.shared.dto.request.LoginRequest;
import com.groceryshop.shared.dto.request.RegisterRequest;
import com.groceryshop.shared.dto.response.AuthResponse;
import com.groceryshop.shared.exception.ResourceNotFoundException;
import com.groceryshop.shared.exception.UserNotAuthenticatedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import java.util.Collections;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoderUtil passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private com.groceryshop.auth.User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.createTestUser();
        registerRequest = TestDataFactory.createTestRegisterRequest();
        loginRequest = TestDataFactory.createTestLoginRequest();
    }

    @Test
    void register_ShouldCreateNewUser_WhenEmailNotExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(com.groceryshop.auth.User.class))).thenReturn(testUser);

        // When
        com.groceryshop.auth.User user = authService.register(registerRequest);

        // Then
        assertNotNull(user);
        assertEquals(testUser.getEmail(), user.getEmail());
        verify(userRepository).save(any(com.groceryshop.auth.User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(com.groceryshop.auth.User.class));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(any(com.groceryshop.auth.User.class))).thenReturn("jwtToken");
        when(jwtUtil.generateRefreshToken(any(com.groceryshop.auth.User.class))).thenReturn("refreshToken");

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwtToken", response.token());
        assertEquals("refreshToken", response.refreshToken());
        verify(jwtUtil).generateToken(testUser);
        verify(jwtUtil).generateRefreshToken(testUser);
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_ShouldThrowException_WhenPasswordInvalid() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
    }

    @Test
    void getCurrentUser_ShouldReturnUser_WhenAuthenticated() {
        // Given
        User springUser = new User(testUser.getEmail(), testUser.getPasswordHash(), true, true, true, true, Collections.emptyList());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(springUser);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        SecurityContextHolder.setContext(securityContext);

        // When
        com.groceryshop.auth.User user = authService.getCurrentUser();

        // Then
        assertNotNull(user);
        assertEquals(testUser.getEmail(), user.getEmail());
    }

    @Test
    void getCurrentUser_ShouldThrowException_WhenNotAuthenticated() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // When & Then
        assertThrows(UserNotAuthenticatedException.class, () -> authService.getCurrentUser());
    }
}
