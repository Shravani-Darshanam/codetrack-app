package com.codetrack;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/tests")
public class TestController {
  private final TestService service; private final CurrentUser me;
  public TestController(TestService s, CurrentUser me) { this.service = s; this.me = me; }

  @GetMapping("/current")
  public ResponseEntity<?> current(Authentication a) {
    return service.getOrCreate(me.get(a)).<ResponseEntity<?>>map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "No problems solved this week yet")));
  }

  @PostMapping("/{id}/submit")
  public ResponseEntity<?> submit(@PathVariable Long id, @RequestBody List<TestService.Answer> answers, Authentication a) {
    return service.submit(me.get(a), id, answers).<ResponseEntity<?>>map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }
}
