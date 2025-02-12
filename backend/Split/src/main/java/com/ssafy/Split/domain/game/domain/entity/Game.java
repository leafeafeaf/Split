package com.ssafy.Split.domain.game.domain.entity;

import com.ssafy.Split.domain.rank.domain.entity.Rank;
import com.ssafy.Split.domain.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "game")
public class Game {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  @Column(name = "game_date", nullable = false)
  private LocalDateTime gameDate;

  @Column(name = "is_skip", nullable = false)
  private Boolean isSkip = false;

  @Column(name = "pose_highscore", nullable = false)
  @DecimalMin("0.00")
  @DecimalMax("100.00")
  private BigDecimal poseHighscore = BigDecimal.ZERO;

  @Column(name = "pose_lowscore", nullable = false)
  @DecimalMin("0.00")
  @DecimalMax("100.00")
  private BigDecimal poseLowscore = BigDecimal.ZERO;

  @Column(name = "pose_avgscore", nullable = false)
  @DecimalMin("0.00")
  @DecimalMax("100.00")
  private BigDecimal poseAvgscore = BigDecimal.ZERO;

  @Column(name = "elbow_angle_score", nullable = false)
  @DecimalMin("0.00")
  @DecimalMax("100.00")
  private BigDecimal elbowAngleScore = BigDecimal.ZERO;

  @Column(name = "arm_stability_score", nullable = false)
  @DecimalMin("0.00")
  @DecimalMax("100.00")
  private BigDecimal armStabilityScore = BigDecimal.ZERO;

  @Column(name = "arm_speed", nullable = false)
  private BigDecimal armSpeed = BigDecimal.ZERO;

  @OneToOne(mappedBy = "game")
  private Rank rank;

  @Column(name = "bowling_score")
  private Integer bowlingScore;

  @Builder
  public Game(User user, LocalDateTime gameDate, Boolean isSkip, BigDecimal poseHighscore,
      BigDecimal poseLowscore, BigDecimal poseAvgscore, BigDecimal elbowAngleScore,
      BigDecimal armStabilityScore, BigDecimal armSpeed, Rank rank, Integer bowlingScore) {
    this.user = user;
    this.gameDate = gameDate;
    this.isSkip = isSkip;
    this.poseHighscore = poseHighscore;
    this.poseLowscore = poseLowscore;
    this.poseAvgscore = poseAvgscore;
    this.elbowAngleScore = elbowAngleScore;
    this.armStabilityScore = armStabilityScore;
    this.armSpeed = armSpeed;
    this.rank = rank;
    this.bowlingScore = bowlingScore;
  }
}

