package com.busease.service;

import com.busease.dto.LoginRequest;
import com.busease.dto.RegisterRequest;
import com.busease.entity.User;
import com.busease.enums.Role;
import com.busease.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerNewUser_success() {
        // Clear pre-loaded data
        userRepository.deleteAll();

        RegisterRequest request = RegisterRequest.builder()
                .name("New User")
                .email("newuser@test.com")
                .password("password123")
                .build();

        String result = authService.register(request);

        assertNotNull(result);
        assertTrue(userRepository.findByEmail("newuser@test.com").isPresent());
    }

    @Test
    void registerDuplicateEmail_throwsException() {
        // Clear pre-loaded data
        userRepository.deleteAll();

        RegisterRequest request = RegisterRequest.builder()
                .name("User One")
                .email("duplicate@test.com")
                .password("password123")
                .build();

        authService.register(request);

        // Same email again
        RegisterRequest duplicateRequest = RegisterRequest.builder()
                .name("User Two")
                .email("duplicate@test.com")
                .password("password456")
                .build();

        assertThrows(RuntimeException.class, () -> authService.register(duplicateRequest));
    }

    @Test
    void loginWithValidCredentials_returnsToken() {
        // Clear and create a user
        userRepository.deleteAll();

        RegisterRequest registerReq = RegisterRequest.builder()
                .name("Login User")
                .email("login@test.com")
                .password("mypassword")
                .build();
        authService.register(registerReq);

        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("login@test.com");
        loginReq.setPassword("mypassword");

        var response = authService.login(loginReq);
        assertNotNull(response);
        assertNotNull(response.getToken());
    }

    @Test
    void loginWithInvalidCredentials_throwsException() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("nonexistent@test.com");
        loginReq.setPassword("wrongpassword");

        assertThrows(Exception.class, () -> authService.login(loginReq));
    }
}
