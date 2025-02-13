package com.ssafy.Split.domain.game.domain.dto.response;

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
public class GameListResponse {
    private String code;
    private int status;
    private String message;
    private String timestamp;
    private GameListData data;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameListData {
        private Integer count;
        private List<GameDetail> gameArr;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameDetail {
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
    }
}
