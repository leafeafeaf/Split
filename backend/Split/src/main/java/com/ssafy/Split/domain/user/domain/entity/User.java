package com.ssafy.Split.domain.user.domain.entity;

import com.ssafy.Split.domain.bowling.domain.entity.Progress;
import com.ssafy.Split.domain.game.domain.entity.Game;
import com.ssafy.Split.domain.rank.domain.entity.Rank;
import com.ssafy.Split.domain.user.domain.dto.request.UpdateUserRequestDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
  @Builder.Default
  private Integer gender = 3;  // 1: 남자, 2: 여자, 3: 기타 또는 비공개

  @Column
  @Builder.Default
  private Integer height = 0;

  @Column(nullable = false, length = 50, unique = true)
  private String nickname;

  @Column(name = "total_game_count", nullable = false)
  @Builder.Default
  private Integer totalGameCount = 0;

  private String highlight;

  @Column(name = "total_pose_highscore", nullable = false)
  @DecimalMin("0.00")
  @DecimalMax("100.00")
  @Builder.Default
  private BigDecimal totalPoseHighscore = BigDecimal.ZERO;

  @Column(name = "total_pose_avgscore", nullable = false)
  @DecimalMin("0.00")
  @DecimalMax("100.00")
  @Builder.Default
  private BigDecimal totalPoseAvgscore = BigDecimal.ZERO;

  @Column(name = "elbow_angle_score", nullable = false)
  @DecimalMin("0.00")
  @DecimalMax("100.00")
  @Builder.Default
  private BigDecimal elbowAngleScore = BigDecimal.ZERO;

  @Column(name = "arm_stability_score", nullable = false)
  @DecimalMin("0.00")
  @DecimalMax("100.00")
  @Builder.Default
  private BigDecimal armStabilityScore = BigDecimal.ZERO;

  @Column(name = "arm_speed_score", nullable = false)
  @Builder.Default
  private BigDecimal armSpeedScore = BigDecimal.ZERO;

  @Column(nullable = false)
  @Builder.Default
  private Integer thema = 2;  // 1: 라이트, 2: 다크

  @Column(name = "curr_bowling_score", nullable = false, columnDefinition = "SMALLINT UNSIGNED DEFAULT 0")
  @Builder.Default
  private Integer currBowlingScore = 0; // 최근 볼링 점수

  @Column(name = "avg_bowling_score", nullable = false, columnDefinition = "SMALLINT UNSIGNED DEFAULT 0")
  @Builder.Default
  private Integer avgBowlingScore = 0; // 평균 볼링 점수

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Game> games;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Progress> progresses;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Rank ranks;


  public void updateHighlight(String highlight) {
    this.highlight = highlight;
  }

  public void createHighlight(String highlight) {
    this.highlight = highlight;
  }

  public void updateThema(Integer thema) {
    this.thema = thema;
  }

  public void updateUser(User user, UpdateUserRequestDto updateRequest) {
    if (updateRequest.getGender() != null) {
      user.setGender(updateRequest.getGender());
    }

    if (updateRequest.getHeight() != null) {
      user.setHeight(updateRequest.getHeight());
    }

    if (updateRequest.getNickname() != null) {
      user.setNickname(updateRequest.getNickname());
    }

    if (updateRequest.getTotalGameCount() != null) {
      user.setTotalGameCount(updateRequest.getTotalGameCount());
    }

    if (updateRequest.getHighlight() != null) {
      user.setHighlight(updateRequest.getHighlight());
    }

    if (updateRequest.getTotalPoseHighscore() != null) {
      user.setTotalPoseHighscore(BigDecimal.valueOf(updateRequest.getTotalPoseHighscore()));
    }

    if (updateRequest.getTotalPoseAvgscore() != null) {
      user.setTotalPoseAvgscore(BigDecimal.valueOf(updateRequest.getTotalPoseAvgscore()));
    }

    if (updateRequest.getElbowAngleScore() != null) {
      user.setElbowAngleScore(BigDecimal.valueOf(updateRequest.getElbowAngleScore()));
    }

    if (updateRequest.getArmStabilityScore() != null) {
      user.setArmStabilityScore(BigDecimal.valueOf(updateRequest.getArmStabilityScore()));
    }

    if (updateRequest.getArmSpeedScore() != null) {
      user.setArmSpeedScore(BigDecimal.valueOf(updateRequest.getArmSpeedScore()));
    }

    if (updateRequest.getThema() != null) {
      user.setThema(updateRequest.getThema());
    }

    if (updateRequest.getCurrBowlingScore() != null) {
      user.setCurrBowlingScore(updateRequest.getCurrBowlingScore());
    }

    if (updateRequest.getAvgBowlingScore() != null) {
      user.setAvgBowlingScore(updateRequest.getAvgBowlingScore());
    }
  }


  public void increaseTotalGameCount() {
    this.totalGameCount += 1;
  }


}