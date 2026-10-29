package com.codetrack;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;
import lombok.*;
@Entity @Getter @Setter
public class WeeklyTest {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(fetch=FetchType.LAZY) private User user;
  private LocalDate weekStart;
  private String status = "PENDING";
  private Integer score;
  @OneToMany(mappedBy="test", cascade=CascadeType.ALL, orphanRemoval=true) private List<TestQuestion> questions = new ArrayList<>();
}
