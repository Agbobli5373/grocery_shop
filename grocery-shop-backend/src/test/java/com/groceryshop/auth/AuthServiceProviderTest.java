package com.groceryshop.auth;

import com.groceryshop.TestDataFactory;
import com.groceryshop.auth.spi.AuthServiceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * SPI Provider Contract Test for AuthServiceProvider.
 * Tests the contract implementation to ensure SPI compatibility.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceProviderTest {

    @Mock
    private UserRepository userRepository;

    private AuthServiceProvider authServiceProvider;
    private com.groceryshop.auth.User testUser;

    @BeforeEach
    void setUp() {
        authServiceProvider = new AuthServiceProviderImpl(userRepository);
        testUser = TestDataFactory.createTestUser();
    }

    @Test
    void findUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<com.groceryshop.auth.User> result = authServiceProvider.findUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        assertEquals(testUser.getEmail(), result.get().getEmail());
    }

    @Test
    void findUserById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<com.groceryshop.auth.User> result = authServiceProvider.findUserById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findUserByEmail_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<com.groceryshop.auth.User> result = authServiceProvider.findUserByEmail("test@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
        assertEquals(testUser.getId(), result.get().getId());
    }

    @Test
    void findUserByEmail_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When
        Optional<com.groceryshop.auth.User> result = authServiceProvider.findUserByEmail("nonexistent@example.com");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findUserByEmail_ShouldHandleNullEmail() {
        // Given
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

        // When
        Optional<com.groceryshop.auth.User> result = authServiceProvider.findUserByEmail(null);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findUserById_ShouldHandleNullId() {
        // Given
        when(userRepository.findById(null)).thenReturn(Optional.empty());

        // When
        Optional<com.groceryshop.auth.User> result = authServiceProvider.findUserById(null);

        // Then
        assertFalse(result.isPresent());
    }
}
