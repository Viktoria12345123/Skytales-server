package skytales.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import skytales.auth.dto.*;
import skytales.auth.model.User;
import skytales.auth.service.JwtService;
import skytales.auth.service.SessionService;
import skytales.auth.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SessionService sessionService;

    public AuthController(UserService userService, BCryptPasswordEncoder passwordEncoder, JwtService jwtService, SessionService sessionService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.sessionService = sessionService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        User user = userService.register(registerRequest, passwordEncoder);

        String jwtToken = jwtService.generateToken(
                user.getId().toString(),
                user.getRole().name(),
                user.getEmail()
        );

        RegisterResponse registerResponse = new RegisterResponse(
                user.getEmail(),
                user.getId().toString(),
                user.getRole().name(),
                jwtToken
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        User user = userService.login(loginRequest);

        String jwtToken = jwtService.generateToken(user.getId().toString(), user.getRole().name(), user.getEmail());

        LoginResponse loginResponse = new LoginResponse(
                user.getEmail(),
                user.getId().toString(),
                user.getRole().name(),
                jwtToken
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
    }


    @GetMapping("/session")
    public ResponseEntity<?> getSession(HttpServletRequest request) {

        try {
            SessionResponse sessionResponse = sessionService.getSessionData(request);
            return ResponseEntity.ok(sessionResponse);

        } catch (SessionAuthenticationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
