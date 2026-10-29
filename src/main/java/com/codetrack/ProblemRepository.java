package com.codetrack;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.*;
public interface ProblemRepository extends JpaRepository<SolvedProblem,Long> {
  List<SolvedProblem> findByUserOrderBySolvedDateDescIdDesc(User u);
  List<SolvedProblem> findByUserAndSolvedDateOrderByIdDesc(User u, LocalDate d);
  List<SolvedProblem> findByUserAndSolvedDateBetween(User u, LocalDate from, LocalDate to);
  long countByUserAndSolvedDate(User u, LocalDate d);
  long countByUser(User u);
  @Query("select distinct p.solvedDate from SolvedProblem p where p.user = ?1")
  List<LocalDate> solvedDates(User u);
}
