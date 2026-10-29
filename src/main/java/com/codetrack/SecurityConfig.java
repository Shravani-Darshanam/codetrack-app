package com.codetrack;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
public class SecurityConfig {
  @Bean PasswordEncoder encoder() { return new BCryptPasswordEncoder(); }
  @Bean SecurityFilterChain chain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
    http.csrf(c -> c.disable())
      .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(a -> a.requestMatchers("/api/auth/**", "/", "/index.html", "/assets/**", "/favicon.ico").permitAll().anyRequest().authenticated())
      .exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> res.sendError(401, "Unauthorized")))
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
