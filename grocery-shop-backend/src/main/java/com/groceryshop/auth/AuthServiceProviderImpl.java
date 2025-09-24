package com.groceryshop.auth;

import com.groceryshop.auth.spi.AuthServiceProvider;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the AuthServiceProvider SPI.
 * This provides access to auth functionality for other modules.
 */
@Service
public class AuthServiceProviderImpl implements AuthServiceProvider {

    private final UserRepository userRepository;

    public AuthServiceProviderImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
