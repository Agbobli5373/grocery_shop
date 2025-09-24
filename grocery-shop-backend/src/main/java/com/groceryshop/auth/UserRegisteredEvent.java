package com.groceryshop.auth;

import org.springframework.context.ApplicationEvent;

public class UserRegisteredEvent extends ApplicationEvent {

    private final Long userId;
    private final String email;
    private final String firstName;
    private final String lastName;

    public UserRegisteredEvent(Object source, Long userId, String email, String firstName, String lastName) {
        super(source);
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}
