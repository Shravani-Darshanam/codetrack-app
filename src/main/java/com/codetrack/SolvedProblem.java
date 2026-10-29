package com.codetrack;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;
@Entity @Getter @Setter
public class SolvedProblem {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(fetch=FetchType.LAZY) @JsonIgnore private User user;
  private String platform, title, url, difficulty, tags;
  @Column(length=2000) private String notes;
  private LocalDate solvedDate;
  private int confidence = 0;      // 0 = never tested, 1 forgot, 2 partly, 3 remembered
  private LocalDate lastReviewed;
}
