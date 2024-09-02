package at.emreeocn.vinservicecheckerserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verify")
public class VerifyController {

    /**
     * Verify the session
     * @param request   The request
     * @return  The session ID
     */
    @GetMapping("")
    public String verify(HttpServletRequest request) {
        return request.getSession().getId();
    }

}
