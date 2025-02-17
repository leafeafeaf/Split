package com.ssafy.Split.domain.rank.domain.entity;

import com.ssafy.Split.domain.game.domain.entity.Game;
import com.ssafy.Split.domain.user.domain.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
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
@IdClass(RankId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "game_rank")
public class Rank {

  @Id
  @OneToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "game_id")
  private Game game;

  @Id
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @JoinColumn(name = "user_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)  // DB 레벨에서 User 삭제 시 Progress 삭제
  private User user;

  @Column(nullable = false)
  private String nickname;

  private String highlight;

  @Column(name = "total_game_count")
  private Integer totalGameCount;

  @Column(name = "game_date", nullable = false)
  private LocalDateTime gameDate;

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

  @Builder
  public Rank(Game game, User user, String nickname, String highlight, Integer totalGameCount,
      LocalDateTime gameDate, BigDecimal poseHighscore, BigDecimal poseLowscore,
      BigDecimal poseAvgscore, BigDecimal elbowAngleScore, BigDecimal armStabilityScore,
      BigDecimal armSpeed) {
    this.game = game;
    this.user = user;
    this.nickname = nickname;
    this.highlight = highlight;
    this.totalGameCount = totalGameCount;
    this.gameDate = gameDate;
    this.poseHighscore = poseHighscore;
    this.poseLowscore = poseLowscore;
    this.poseAvgscore = poseAvgscore;
    this.elbowAngleScore = elbowAngleScore;
    this.armStabilityScore = armStabilityScore;
    this.armSpeed = armSpeed;
  }
}
