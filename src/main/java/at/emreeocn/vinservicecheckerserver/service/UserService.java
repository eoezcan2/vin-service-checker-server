package at.emreeocn.vinservicecheckerserver.service;

import at.emreeocn.vinservicecheckerserver.dto.request.AuthRequest;
import at.emreeocn.vinservicecheckerserver.dto.request.LoginUserRequest;
import at.emreeocn.vinservicecheckerserver.model.UserEntity;
import at.emreeocn.vinservicecheckerserver.repository.UserRepository;
import at.emreeocn.vinservicecheckerserver.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

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
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword()));
        if (auth.isAuthenticated()) return jwtService.generateToken(body.getUsername());
        return "Failed";
    }
}
