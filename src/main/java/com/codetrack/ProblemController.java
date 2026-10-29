package com.codetrack;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/problems")
public class ProblemController {
  record ProblemReq(@NotBlank String platform, @NotBlank String title, String url, String difficulty, String tags, String notes, LocalDate solvedDate) {}
  private final ProblemRepository problems; private final TestQuestionRepository questions; private final CurrentUser me;
  public ProblemController(ProblemRepository p, TestQuestionRepository q, CurrentUser me) { this.problems = p; this.questions = q; this.me = me; }

  @PostMapping
  public SolvedProblem add(@Valid @RequestBody ProblemReq r, Authentication a) {
    SolvedProblem p = new SolvedProblem();
    p.setUser(me.get(a)); p.setPlatform(r.platform()); p.setTitle(r.title()); p.setUrl(r.url());
    p.setDifficulty(r.difficulty()); p.setTags(r.tags()); p.setNotes(r.notes());
    p.setSolvedDate(r.solvedDate() != null ? r.solvedDate() : LocalDate.now());
    return problems.save(p);
  }

  @GetMapping
  public List<SolvedProblem> list(@RequestParam(required=false) LocalDate date, Authentication a) {
    User u = me.get(a);
    return date != null ? problems.findByUserAndSolvedDateOrderByIdDesc(u, date) : problems.findByUserOrderBySolvedDateDescIdDesc(u);
  }

  @DeleteMapping("/{id}") @Transactional
  public ResponseEntity<?> delete(@PathVariable Long id, Authentication a) {
    User u = me.get(a);
    return problems.findById(id).filter(p -> p.getUser().getId().equals(u.getId())).map(p -> {
      questions.deleteByProblem(p); problems.delete(p); return ResponseEntity.noContent().build();
    }).orElse(ResponseEntity.notFound().build());
  }
}
