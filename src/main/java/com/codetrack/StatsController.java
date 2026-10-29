package com.codetrack;
import java.time.LocalDate;
import java.util.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/stats")
public class StatsController {
  private final ProblemRepository problems; private final CurrentUser me;
  public StatsController(ProblemRepository p, CurrentUser me) { this.problems = p; this.me = me; }

  @GetMapping
  public Map<String,Object> stats(Authentication a) {
    User u = me.get(a); LocalDate today = LocalDate.now();
    Set<LocalDate> days = new HashSet<>(problems.solvedDates(u));
    LocalDate d = days.contains(today) ? today : today.minusDays(1);
    int streak = 0; while (days.contains(d)) { streak++; d = d.minusDays(1); }
    Map<String,Long> last7 = new LinkedHashMap<>();
    for (int i = 6; i >= 0; i--) { LocalDate x = today.minusDays(i); last7.put(x.toString(), problems.countByUserAndSolvedDate(u, x)); }
    return Map.of("today", problems.countByUserAndSolvedDate(u, today), "target", u.getDailyTarget(),
      "streak", streak, "total", problems.countByUser(u), "last7", last7);
  }
}
