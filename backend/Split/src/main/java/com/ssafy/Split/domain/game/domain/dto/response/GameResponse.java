package com.ssafy.Split.domain.game.domain.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {

  private String code;
  private int status;
  private String message;
  private GameData data;
  private String timestamp;

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GameData {

    private Integer id;
    private Integer userId;
    private String gameDate;
    private Integer isSkip;
    private BigDecimal poseHighscore;
    private BigDecimal poseLowscore;
    private BigDecimal poseAvgscore;
    private BigDecimal elbowAngleScore;
    private BigDecimal armStabilityScore;
    private BigDecimal armSpeed;
    private Integer bowlingScore;
  }
}
