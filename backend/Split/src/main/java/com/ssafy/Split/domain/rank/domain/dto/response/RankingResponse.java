package com.ssafy.Split.domain.rank.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingResponse {
    private String code;
    private int status;
    private String message;
    private List<RankData> data;
    private String timestamp;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankData {
        private Integer gameId;
        private Integer userId;
        private String nickname;
        private String highlight;
        private Integer totalGameCount;
        private String gameDate;
        private BigDecimal poseHighscore;
        private BigDecimal poseLowscore;
        private BigDecimal poseAvgscore;
        private BigDecimal elbowAngleScore;
        private BigDecimal armStabilityScore;
        private BigDecimal armSpeed;
    }
}
