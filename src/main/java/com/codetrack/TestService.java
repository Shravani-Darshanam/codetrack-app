package com.codetrack;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class TestService {
  public record QView(Long id, String title, String platform, String url, String difficulty, Integer rating) {}
  public record TestView(Long id, LocalDate weekStart, String status, Integer score, List<QView> questions) {}
  public record Answer(Long questionId, int rating) {}
  private static final int MAX_QUESTIONS = 10;
  private final WeeklyTestRepository tests; private final ProblemRepository problems; private final UserRepository users;
  public TestService(WeeklyTestRepository t, ProblemRepository p, UserRepository u) { this.tests = t; this.problems = p; this.users = u; }

  /** Every Sunday 8 AM: create this week's test for every user. */
  @Scheduled(cron = "0 0 8 * * SUN") @Transactional
  public void generateForAll() { users.findAll().forEach(this::getOrCreate); }

  @Transactional
  public Optional<TestView> getOrCreate(User u) {
    LocalDate today = LocalDate.now(), week = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    WeeklyTest t = tests.findByUserAndWeekStart(u, week).orElse(null);
    if (t == null) {
      List<SolvedProblem> pool = new ArrayList<>(problems.findByUserAndSolvedDateBetween(u, today.minusDays(6), today));
      if (pool.isEmpty()) return Optional.empty();
      Collections.shuffle(pool);
      pool.sort(Comparator.comparingInt(SolvedProblem::getConfidence)); // weakest first
      t = new WeeklyTest(); t.setUser(u); t.setWeekStart(week);
      for (SolvedProblem p : pool.subList(0, Math.min(MAX_QUESTIONS, pool.size()))) {
        TestQuestion q = new TestQuestion(); q.setTest(t); q.setProblem(p); t.getQuestions().add(q);
      }
      t = tests.save(t);
    }
    return Optional.of(view(t));
  }

  @Transactional
  public Optional<TestView> submit(User u, Long testId, List<Answer> answers) {
    return tests.findById(testId).filter(t -> t.getUser().getId().equals(u.getId())).map(t -> {
      Map<Long,Integer> byId = new HashMap<>();
      for (Answer a : answers) byId.put(a.questionId(), Math.max(1, Math.min(3, a.rating())));
      int sum = 0;
      for (TestQuestion q : t.getQuestions()) {
        int r = byId.getOrDefault(q.getId(), 1);
        q.setRating(r); q.getProblem().setConfidence(r); q.getProblem().setLastReviewed(LocalDate.now());
        sum += r - 1;
      }
      t.setScore(t.getQuestions().isEmpty() ? 0 : sum * 100 / (2 * t.getQuestions().size()));
      t.setStatus("COMPLETED");
      return view(tests.save(t));
    });
  }

  private TestView view(WeeklyTest t) {
    List<QView> qs = t.getQuestions().stream().map(q -> new QView(q.getId(), q.getProblem().getTitle(), q.getProblem().getPlatform(),
      q.getProblem().getUrl(), q.getProblem().getDifficulty(), q.getRating())).toList();
    return new TestView(t.getId(), t.getWeekStart(), t.getStatus(), t.getScore(), qs);
  }
}
