package com.codetrack;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="users") @Getter @Setter
public class User {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private String name;
  @Column(unique=true,nullable=false) private String email;
  @JsonIgnore private String password;
  private int dailyTarget = 5;
}
