package com.ssafy.Split.domain.user.domain.entity;

import com.ssafy.Split.domain.bowling.domain.entity.Progress;
import com.ssafy.Split.domain.game.domain.entity.Game;
import com.ssafy.Split.domain.rank.domain.entity.Rank;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 128)
    @Email
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Integer gender;  // 1: 남자, 2: 여자, 3: 기타 또는 비공개

    @Column
    private Integer height;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "total_game_count", nullable = false)
    private Integer totalGameCount = 0;

    private String highlight;

    @Column(name = "total_pose_highscore", nullable = false)
    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal totalPoseHighscore = BigDecimal.ZERO;

    @Column(name = "total_pose_avgscore", nullable = false)
    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal totalPoseAvgscore = BigDecimal.ZERO;

    @Column(name = "elbow_angle_score", nullable = false)
    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal elbowAngleScore = BigDecimal.ZERO;

    @Column(name = "arm_stability_score", nullable = false)
    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal armStabilityScore = BigDecimal.ZERO;

    @Column(name = "arm_speed_score", nullable = false)
    private BigDecimal armSpeedScore = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer thema = 2;  // 1: 라이트, 2: 다크

    @OneToMany(mappedBy = "user")
    private List<Game> games = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Progress> progresses = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Rank> ranks = new ArrayList<>();


    public void updateHighlight(String highlight) {
        this.highlight = highlight;
    }
    public void createHighlight(String highlight) {
        this.highlight = highlight;
    }
    public void updateThema(Integer thema) {
        this.thema = thema;
    }
}