package com.ssafy.Split.bowling.domain.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "frame")
public class Frame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "progress_id", nullable = false)
    private Progress progress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_number", nullable = false)
    private Device device;

    @Column(nullable = false)
    private Integer num;

    private String video;

    @Column(name = "is_skip", nullable = false)
    private Boolean isSkip = false;

    @Column(nullable = false)
    private String feedback = "";

    @Column(name = "pose_socre", nullable = false)
    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal poseSocre = BigDecimal.ZERO;

    @Column(name = "elbow_angle_score", nullable = false)
    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal elbowAngleScore = BigDecimal.ZERO;

    @Column(name = "arm_stability_score", nullable = false)
    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal armStabilityScore = BigDecimal.ZERO;

    @Column(name = "speed", nullable = false)
    private BigDecimal speed = BigDecimal.ZERO;

    @Builder
    public Frame(Progress progress, Device device, Integer num, String video, Boolean isSkip, String feedback, BigDecimal poseSocre, BigDecimal elbowAngleScore, BigDecimal armStabilityScore, BigDecimal speed) {
        this.progress = progress;
        this.device = device;
        this.num = num;
        this.video = video;
        this.isSkip = false;
        this.feedback = "";
        this.poseSocre = BigDecimal.ZERO;
        this.elbowAngleScore = BigDecimal.ZERO;
        this.armStabilityScore = BigDecimal.ZERO;
        this.speed = BigDecimal.ZERO;
    }
}
