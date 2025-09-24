package com.groceryshop.auth;

import com.groceryshop.shared.dto.request.LoginRequest;
import com.groceryshop.shared.dto.request.RegisterRequest;
import com.groceryshop.shared.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    User register(RegisterRequest request);

    void logout(String token);

    boolean validateToken(String token);

    User getCurrentUser();
}
