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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<RegisterUserResponse> register(@RequestBody RegisterUserRequest body) {
        if (userService.existsByUsername(body.getUsername())) return ResponseEntity.badRequest().build();
        if (userService.existsByEmail(body.getEmail())) return ResponseEntity.badRequest().build();

        UserEntity user = new UserEntity();
        user.setUsername(body.getUsername());
        user.setEmail(body.getEmail());
        user.setPassword(body.getPassword());
        String status = userService.register(user);
        if (status != null) return ResponseEntity.badRequest().build();

        RegisterUserResponse response = new RegisterUserResponse();
        response.setUsername(body.getUsername());
        response.setEmail(body.getEmail());
        response.setToken(userService.verify(body));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(@RequestBody LoginUserRequest body) {
        logger.info("Login attempt for user: {}", body.getUsername());
        
        LoginUserResponse response = new LoginUserResponse();
        String token = userService.verify(body);
        response.setToken(token);
        
        if (token.equals("Failed")) {
            logger.warn("Login failed for user: {}", body.getUsername());
            return ResponseEntity.badRequest().build();
        } else {
            logger.info("Login successful for user: {}", body.getUsername());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Create a test user for debugging (only in development)
     */
    @PostMapping("/create-test-user")
    public ResponseEntity<String> createTestUser() {
        userService.createTestUserIfNotExists();
        return ResponseEntity.ok("Test user created or already exists. Username: testuser, Password: password123");
    }

}
