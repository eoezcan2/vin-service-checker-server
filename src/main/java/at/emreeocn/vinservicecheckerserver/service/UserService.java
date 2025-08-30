package at.emreeocn.vinservicecheckerserver.service;

import at.emreeocn.vinservicecheckerserver.dto.request.AuthRequest;
import at.emreeocn.vinservicecheckerserver.dto.request.LoginUserRequest;
import at.emreeocn.vinservicecheckerserver.model.UserEntity;
import at.emreeocn.vinservicecheckerserver.repository.UserRepository;
import at.emreeocn.vinservicecheckerserver.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public String register(UserEntity user) {
        // Validate input
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return "Username is required";
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return "Email is required";
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return "Password is required";
        }
        if (user.getPassword().length() < 6) {
            return "Password must be at least 6 characters long";
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            return "Invalid email format";
        }

        // Check for existing user
        if (existsByUsername(user.getUsername().trim())) {
            return "Username already exists";
        }
        if (existsByEmail(user.getEmail().trim())) {
            return "Email already exists";
        }

        try {
            user.setUsername(user.getUsername().trim());
            user.setEmail(user.getEmail().trim());
            user.setPassword(encoder.encode(user.getPassword()));
            userRepository.save(user);
            logger.info("User registered successfully: {}", user.getUsername());
            return null;
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage(), e);
            return "Registration failed due to server error";
        }
    }

    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userRepository.findByUsername(username.trim()) != null;
    }

    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userRepository.findByEmail(email.trim()) != null;
    }

    public UserEntity findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userRepository.findByUsername(username.trim());
    }

    public String verify(AuthRequest body) {
        if (body == null || body.getUsername() == null || body.getPassword() == null) {
            logger.warn("Invalid authentication request: missing credentials");
            return "Failed";
        }

        logger.info("Attempting to verify user: {}", body.getUsername());
        
        // Check if user exists first
        UserEntity user = userRepository.findByUsername(body.getUsername().trim());
        if (user == null) {
            logger.warn("User not found: {}", body.getUsername());
            return "Failed";
        }
        
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.getUsername().trim(), body.getPassword())
            );
            
            if (auth.isAuthenticated()) {
                logger.info("User authenticated successfully: {}", body.getUsername());
                return jwtService.generateToken(body.getUsername().trim());
            } else {
                logger.warn("Authentication failed for user: {}", body.getUsername());
                return "Failed";
            }
        } catch (BadCredentialsException e) {
            logger.warn("Bad credentials for user: {}", body.getUsername());
            return "Failed";
        } catch (Exception e) {
            logger.error("Authentication error for user {}: {}", body.getUsername(), e.getMessage(), e);
            return "Failed";
        }
    }

    public boolean userExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userRepository.findByUsername(username.trim()) != null;
    }

    public void createTestUserIfNotExists() {
        if (!userExists("testuser")) {
            logger.info("Creating test user: testuser");
            try {
                UserEntity testUser = new UserEntity();
                testUser.setUsername("testuser");
                testUser.setEmail("test@example.com");
                testUser.setPassword("password123");
                String result = register(testUser);
                if (result == null) {
                    logger.info("Test user created successfully");
                } else {
                    logger.warn("Failed to create test user: {}", result);
                }
            } catch (Exception e) {
                logger.error("Error creating test user: {}", e.getMessage(), e);
            }
        }
    }
}
