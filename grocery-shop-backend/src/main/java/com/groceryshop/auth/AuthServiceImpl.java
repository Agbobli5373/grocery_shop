package com.groceryshop.auth;

import com.groceryshop.shared.dto.request.LoginRequest;
import com.groceryshop.shared.dto.request.RegisterRequest;
import com.groceryshop.shared.dto.response.AuthResponse;
import com.groceryshop.shared.exception.AccountInactiveException;
import com.groceryshop.shared.exception.InvalidCredentialsException;
import com.groceryshop.shared.exception.ResourceNotFoundException;
import com.groceryshop.shared.exception.UserAlreadyExistsException;
import com.groceryshop.shared.exception.UserNotAuthenticatedException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoderUtil passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ApplicationEventPublisher eventPublisher;

    public AuthServiceImpl(UserRepository userRepository,
                          PasswordEncoderUtil passwordEncoder,
                          JwtUtil jwtUtil,
                          ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccountInactiveException("Account is not active");
        }

        String token = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new AuthResponse(
            token,
            refreshToken,
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole()
        );
    }

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = new User(
            request.email(),
            passwordEncoder.encode(request.password()),
            request.firstName(),
            request.lastName()
        );

        User savedUser = userRepository.save(user);

        // Publish user-registered event
        eventPublisher.publishEvent(new UserRegisteredEvent(
            this,
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getFirstName(),
            savedUser.getLastName()
        ));

        return savedUser;
    }

    @Override
    public void logout(String token) {
        // In a stateless JWT implementation, logout is handled client-side
        // For enhanced security, you could implement token blacklisting
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User userDetails) {
            return userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }
        throw new UserNotAuthenticatedException("No authenticated user found");
    }
}
