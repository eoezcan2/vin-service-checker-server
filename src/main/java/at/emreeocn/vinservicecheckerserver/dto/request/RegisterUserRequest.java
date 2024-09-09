package at.emreeocn.vinservicecheckerserver.dto.request;

public class RegisterUserRequest implements AuthRequest {

    private String username;
    private String email;
    private String password;

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
