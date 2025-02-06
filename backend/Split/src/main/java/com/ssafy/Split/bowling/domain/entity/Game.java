package com.ssafy.Split.bowling.domain.entity;

import com.ssafy.Split.rank.domain.entity.Rank;
import com.ssafy.Split.user.domain.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "game_date", nullable = false)
    private LocalDateTime gameDate;

    @Column(name = "is_skip", nullable = false)
    private Boolean isSkip = false;

    @Column(name = "pose_highscore", nullable = false)
    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal poseHighscore = BigDecimal.ZERO;

    @Column(name = "pose_lowscore", nullable = false)
    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal poseLowscore = BigDecimal.ZERO;

    @Column(name = "pose_avgscore", nullable = false)
    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal poseAvgscore = BigDecimal.ZERO;

    @Column(name = "elbow_angle_score", nullable = false)
    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal elbowAngleScore = BigDecimal.ZERO;

    @Column(name = "arm_stability_score", nullable = false)
    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal armStabilityScore = BigDecimal.ZERO;

    @Column(name = "arm_speed", nullable = false)
    private BigDecimal armSpeed = BigDecimal.ZERO;

    @OneToOne(mappedBy = "game")
    private Rank rank;
}

