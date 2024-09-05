package at.emreeocn.vinservicecheckerserver.controller;

import at.emreeocn.vinservicecheckerserver.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verify")
public class VerifyController {

    @Autowired
    private JwtService jwtService;

    @GetMapping("")
    public ResponseEntity<String> verify(HttpServletRequest request) {
        return ResponseEntity.ok("Token is valid for " + jwtService.extractExpiration(request.getHeader("Authorization").split(" ")[1]).toString());
    }

}
