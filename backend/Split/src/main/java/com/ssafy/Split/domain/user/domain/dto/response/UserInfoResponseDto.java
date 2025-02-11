package com.ssafy.Split.domain.user.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDto {
    private String code;
    private int status;
    private String message;
    private String timestamp;

    private UserData data; // ✅ 유저 정보를 담는 내부 클래스

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserData {
        private Integer id;
        private String email;
        private Integer gender;
        private Integer height;
        private String nickname;
        private Integer totalGameCount;
        private String highlight;
        private Double totalPoseHighscore;
        private Double totalPoseAvgscore;
        private Double elbowAngleScore;
        private Double armStabilityScore;
        private Double armSpeedScore;
        private Integer thema;
        private Integer currBowlingScore;
        private Integer avgBowlingScore;
    }
}
