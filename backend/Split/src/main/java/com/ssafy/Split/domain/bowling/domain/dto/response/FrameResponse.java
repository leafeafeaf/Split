package com.ssafy.Split.domain.bowling.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrameResponse {
    private String code;
    private int status;
    private String message;
    private FrameData data;
    private String timestamp;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FrameData {
        private Integer id;
        private Integer progressId;
        private Integer serialNumber;
        private Integer num;
        private String video;
        private Integer isSkip;
        private String feedback;
        private BigDecimal poseScore;
        private BigDecimal elbowAngleScore;
        private BigDecimal armStabilityScore;
        private BigDecimal speed;
    }
}
