package at.emreeocn.vinservicecheckerserver.controller;

import at.emreeocn.vinservicecheckerserver.dto.request.LoginUserRequest;
import at.emreeocn.vinservicecheckerserver.dto.request.RegisterUserRequest;
import at.emreeocn.vinservicecheckerserver.dto.response.LoginUserResponse;
import at.emreeocn.vinservicecheckerserver.dto.response.RegisterUserResponse;
import at.emreeocn.vinservicecheckerserver.model.UserEntity;
import at.emreeocn.vinservicecheckerserver.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Register a new user
     * @param body  The user data
     * @return  The registered user
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserRequest body) {
        logger.info("Registration attempt for user: {}", body.getUsername());
        
        try {
            // Validate request body
            if (body == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Request body is required"));
            }
            if (body.getUsername() == null || body.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username is required"));
            }
            if (body.getEmail() == null || body.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }
            if (body.getPassword() == null || body.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
            }

            // Check for existing user
            if (userService.existsByUsername(body.getUsername().trim())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Username already exists"));
            }
            if (userService.existsByEmail(body.getEmail().trim())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email already exists"));
            }

            // Create user entity
            UserEntity user = new UserEntity();
            user.setUsername(body.getUsername().trim());
            user.setEmail(body.getEmail().trim());
            user.setPassword(body.getPassword());

            // Register user
            String status = userService.register(user);
            if (status != null) {
                return ResponseEntity.badRequest().body(Map.of("error", status));
            }

            // Generate token and response
            String token = userService.verify(body);
            if ("Failed".equals(token)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to generate authentication token"));
            }

            RegisterUserResponse response = new RegisterUserResponse();
            response.setUsername(body.getUsername().trim());
            response.setEmail(body.getEmail().trim());
            response.setToken(token);
            
            logger.info("User registered successfully: {}", body.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error during registration: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Registration failed due to server error"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserRequest body) {
        logger.info("Login attempt for user: {}", body.getUsername());
        
        try {
            // Validate request body
            if (body == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Request body is required"));
            }
            if (body.getUsername() == null || body.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username is required"));
            }
            if (body.getPassword() == null || body.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
            }
            
            LoginUserResponse response = new LoginUserResponse();
            String token = userService.verify(body);
            
            if ("Failed".equals(token)) {
                logger.warn("Login failed for user: {}", body.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
            } else {
                response.setToken(token);
                logger.info("Login successful for user: {}", body.getUsername());
                return ResponseEntity.ok(response);
            }
            
        } catch (Exception e) {
            logger.error("Error during login: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Login failed due to server error"));
        }
    }

    /**
     * Create a test user for debugging (only in development)
     */
    @PostMapping("/create-test-user")
    public ResponseEntity<?> createTestUser() {
        try {
            userService.createTestUserIfNotExists();
            return ResponseEntity.ok(Map.of("message", "Test user created or already exists. Username: testuser, Password: password123"));
        } catch (Exception e) {
            logger.error("Error creating test user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to create test user"));
        }
    }
}
