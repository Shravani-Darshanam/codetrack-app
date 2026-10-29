package com.codetrack;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
  record RegisterReq(@NotBlank String name, @Email @NotBlank String email, @Size(min=6) String password) {}
  record LoginReq(@NotBlank String email, @NotBlank String password) {}
  private final UserRepository users; private final PasswordEncoder enc; private final JwtUtil jwt;
  public AuthController(UserRepository users, PasswordEncoder enc, JwtUtil jwt) { this.users = users; this.enc = enc; this.jwt = jwt; }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterReq r) {
    String email = r.email().toLowerCase();
    if (users.findByEmail(email).isPresent()) return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
    User u = new User(); u.setName(r.name()); u.setEmail(email); u.setPassword(enc.encode(r.password()));
    users.save(u);
    return ResponseEntity.ok(Map.of("token", jwt.generate(email), "name", u.getName()));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginReq r) {
    String email = r.email().toLowerCase();
    return users.findByEmail(email).filter(u -> enc.matches(r.password(), u.getPassword()))
      .<ResponseEntity<?>>map(u -> ResponseEntity.ok(Map.of("token", jwt.generate(email), "name", u.getName())))
      .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "Invalid email or password")));
  }
}
