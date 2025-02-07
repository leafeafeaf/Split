package com.ssafy.Split.domain.bowling.domain.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrameUploadRequest {
    private Boolean isSkip;
    private String feedback;

    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal poseScore;

    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal elbowAngleScore;

    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal armStabilityScore;

    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal speed;
}
