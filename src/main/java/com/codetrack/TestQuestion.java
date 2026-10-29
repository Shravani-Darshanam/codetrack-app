package com.codetrack;
import jakarta.persistence.*;
import lombok.*;
@Entity @Getter @Setter
public class TestQuestion {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(fetch=FetchType.LAZY) private WeeklyTest test;
  @ManyToOne private SolvedProblem problem;
  private Integer rating;
}
