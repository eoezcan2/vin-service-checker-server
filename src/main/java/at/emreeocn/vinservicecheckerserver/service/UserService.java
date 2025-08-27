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

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public String register(UserEntity user) {
        if (existsByUsername(user.getUsername())) return "Username already exists";
        if (existsByEmail(user.getEmail())) return "Email already exists";
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return null;
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String verify(AuthRequest body) {
        logger.info("Attempting to verify user: {}", body.getUsername());
        
        // Check if user exists first
        UserEntity user = userRepository.findByUsername(body.getUsername());
        if (user == null) {
            logger.warn("User not found: {}", body.getUsername());
            return "Failed";
        }
        
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword())
            );
            
            if (auth.isAuthenticated()) {
                logger.info("User authenticated successfully: {}", body.getUsername());
                return jwtService.generateToken(body.getUsername());
            } else {
                logger.warn("Authentication failed for user: {}", body.getUsername());
                return "Failed";
            }
        } catch (BadCredentialsException e) {
            logger.warn("Bad credentials for user: {}", body.getUsername());
            return "Failed";
        } catch (Exception e) {
            logger.error("Authentication error for user {}: {}", body.getUsername(), e.getMessage());
            return "Failed";
        }
    }

    public boolean userExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public void createTestUserIfNotExists() {
        if (!userExists("testuser")) {
            logger.info("Creating test user: testuser");
            UserEntity testUser = new UserEntity();
            testUser.setUsername("testuser");
            testUser.setEmail("test@example.com");
            testUser.setPassword("password123");
            register(testUser);
            logger.info("Test user created successfully");
        }
    }
}
