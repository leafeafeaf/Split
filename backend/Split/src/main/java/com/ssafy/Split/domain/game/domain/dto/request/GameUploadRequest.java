package com.ssafy.Split.domain.game.domain.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameUploadRequest {

  private Integer serialNum;
  private Integer userId;

  private Integer isSkip;

  @DecimalMin("0.00")
  @DecimalMax("100.00")
  private BigDecimal poseHighscore;

  @DecimalMin("0.00")
  @DecimalMax("100.00")
  private BigDecimal poseLowscore;

  @DecimalMin("0.00")
  @DecimalMax("100.00")
  private BigDecimal poseAvgscore;

  @DecimalMin("0.00")
  @DecimalMax("100.00")
  private BigDecimal elbowAngleScore;

  @DecimalMin("0.00")
  @DecimalMax("100.00")
  private BigDecimal armStabilityScore;

  @DecimalMin("0.00")
  @DecimalMax("100.00")
  private BigDecimal armSpeed;


}
