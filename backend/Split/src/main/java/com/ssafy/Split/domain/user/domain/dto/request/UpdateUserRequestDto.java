package com.ssafy.Split.domain.user.domain.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequestDto {

    @Min(1)
    @Max(3)
    private Integer gender; // 1: 남자, 2: 여자, 3: 비공개

    @Min(50) // 최소 키 제한 (예제: 50cm)
    @Max(250) // 최대 키 제한 (예제: 250cm)
    private Integer height;

    private String nickname;

    @Min(0)
    private Integer totalGameCount;

    private String highlight;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    private Double totalPoseHighscore;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    private Double totalPoseAvgscore;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    private Double elbowAngleScore;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    private Double armStabilityScore;

    @DecimalMin("0.00")
    @DecimalMax("100.00")
    private Double armSpeedScore;

    @Min(1)
    @Max(2)
    private Integer thema; // 1: 라이트, 2: 다크 (기본값: 2)

    @Min(0)
    private Integer currBowlingScore;

    @Min(0)
    private Integer avgBowlingScore;
}
