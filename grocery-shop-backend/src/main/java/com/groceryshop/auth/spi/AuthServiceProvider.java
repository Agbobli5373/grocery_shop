package com.groceryshop.auth.spi;

import com.groceryshop.auth.User;

import java.util.Optional;

/**
 * Service Provider Interface for authentication services.
 * This interface defines the contract that other modules can use to interact with auth functionality.
 */
public interface AuthServiceProvider {

    /**
     * Finds a user by their ID.
     *
     * @param userId the user ID
     * @return Optional containing the user if found
     */
    Optional<User> findUserById(Long userId);

    /**
     * Finds a user by their email address.
     *
     * @param email the email address
     * @return Optional containing the user if found
     */
    Optional<User> findUserByEmail(String email);
}
