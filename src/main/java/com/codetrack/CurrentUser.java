package com.codetrack;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
@Component
public class CurrentUser {
  private final UserRepository users;
  public CurrentUser(UserRepository users) { this.users = users; }
  public User get(Authentication a) { return users.findByEmail(a.getName()).orElseThrow(); }
}
