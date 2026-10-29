package com.codetrack;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TestQuestionRepository extends JpaRepository<TestQuestion,Long> { void deleteByProblem(SolvedProblem p); }
