package at.emreeocn.vinservicecheckerserver.controller;

import at.emreeocn.vinservicecheckerserver.dto.request.LoginUserRequest;
import at.emreeocn.vinservicecheckerserver.dto.request.RegisterUserRequest;
import at.emreeocn.vinservicecheckerserver.dto.response.LoginUserResponse;
import at.emreeocn.vinservicecheckerserver.dto.response.RegisterUserResponse;
import at.emreeocn.vinservicecheckerserver.model.UserEntity;
import at.emreeocn.vinservicecheckerserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

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
        LoginUserResponse response = new LoginUserResponse();
        response.setToken(userService.verify(body));
        return response.getToken().equals("Failed") ? ResponseEntity.badRequest().build() : ResponseEntity.ok(response);
    }

}
