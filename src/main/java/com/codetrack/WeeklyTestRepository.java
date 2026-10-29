package com.codetrack;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface WeeklyTestRepository extends JpaRepository<WeeklyTest,Long> { Optional<WeeklyTest> findByUserAndWeekStart(User u, LocalDate w); }
