package com.codetrack;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
@Component
public class JwtFilter extends OncePerRequestFilter {
  private final JwtUtil jwt;
  public JwtFilter(JwtUtil jwt) { this.jwt = jwt; }
  @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
    String h = req.getHeader("Authorization");
    if (h != null && h.startsWith("Bearer ")) {
      try {
        String email = jwt.subject(h.substring(7));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(email, null, List.of()));
      } catch (JwtException | IllegalArgumentException ignored) {}
    }
    chain.doFilter(req, res);
  }
}
