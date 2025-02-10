package com.ssafy.Split.domain.user.domain.entity;

import com.ssafy.Split.domain.bowling.domain.entity.Progress;
import com.ssafy.Split.domain.game.domain.entity.Game;
import com.ssafy.Split.domain.rank.domain.entity.Rank;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 128, unique = true)
  @Email
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private Integer gender = 3;  // 1: 남자, 2: 여자, 3: 기타 또는 비공개

  @Column
  private Integer height = 0;

  @Column(nullable = false, length = 50, unique = true)
  private String nickname;

  @Column(name = "total_game_count", nullable = false)
  private Integer totalGameCount = 0;

  private String highlight;

  @Column(name = "total_pose_highscore", nullable = false)
  @DecimalMin("0.00")
  @DecimalMax("100.00")
  private BigDecimal totalPoseHighscore = BigDecimal.ZERO;

  @Column(name = "total_pose_avgscore", nullable = false)
  @DecimalMin("0.00")
  @DecimalMax("100.00")
  private BigDecimal totalPoseAvgscore = BigDecimal.ZERO;

  @Column(name = "elbow_angle_score", nullable = false)
  @DecimalMin("0.00")
  @DecimalMax("100.00")
  private BigDecimal elbowAngleScore = BigDecimal.ZERO;

  @Column(name = "arm_stability_score", nullable = false)
  @DecimalMin("0.00")
  @DecimalMax("100.00")
  private BigDecimal armStabilityScore = BigDecimal.ZERO;

  @Column(name = "arm_speed_score", nullable = false)
  private BigDecimal armSpeedScore = BigDecimal.ZERO;

  @Column(nullable = false)
  private Integer thema = 2;  // 1: 라이트, 2: 다크

  @OneToMany(mappedBy = "user")
  private List<Game> games;

  @OneToMany(mappedBy = "user")
  private List<Progress> progresses;

  @OneToMany(mappedBy = "user")
  private List<Rank> ranks;


  public void updateHighlight(String highlight) {
    this.highlight = highlight;
  }

  public void createHighlight(String highlight) {
    this.highlight = highlight;
  }

  public void updateThema(Integer thema) {
    this.thema = thema;
  }

  @Builder
  public User(Integer id, String email, String password, Integer gender, Integer height,
      String nickname) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.gender = gender;
    this.height = height;
    this.nickname = nickname;
  }
}