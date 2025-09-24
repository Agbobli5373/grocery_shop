package com.groceryshop.shared.dto.response;

import com.groceryshop.auth.UserRole;

public record AuthResponse(
    String token,
    String refreshToken,
    String email,
    String firstName,
    String lastName,
    UserRole role
) {}
