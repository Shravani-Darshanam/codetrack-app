package com.codetrack;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class JwtUtil {
  private final Key key; private final long exp;
  JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms}") long exp) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.exp = exp;
  }
  public String generate(String email) {
    return Jwts.builder().setSubject(email).setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis()+exp)).signWith(key, SignatureAlgorithm.HS256).compact();
  }
  public String subject(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
  }
}
