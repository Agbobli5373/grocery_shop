package com.groceryshop.shared.dto.response;

import com.groceryshop.auth.UserRole;
import com.groceryshop.auth.UserStatus;

import java.time.LocalDateTime;

/**
 * DTO returned to clients for user information (does NOT include password hash)
 */
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String fullName;

    public UserResponse() {}

    public UserResponse(Long id, String email, String firstName, String lastName, UserRole role, UserStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.fullName = firstName + " " + lastName;
    }

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public UserRole getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getFullName() { return fullName; }

    // Setters (for frameworks if needed)
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setRole(UserRole role) { this.role = role; }
    public void setStatus(UserStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}

