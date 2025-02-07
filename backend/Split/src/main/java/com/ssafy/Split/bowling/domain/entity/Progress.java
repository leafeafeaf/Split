package com.ssafy.Split.bowling.domain.entity;

import com.ssafy.Split.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "progress")
public class Progress {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_number", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "frame_count", nullable = false)
    private Integer frameCount;

    @Column(nullable = false)
    private LocalDateTime time;

    @OneToMany(mappedBy = "progress")
    @Builder.Default
    private List<Frame> frames = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        if (this.time == null) {
            this.time = LocalDateTime.now();
        }
    }

    public void updateFrameCount(Integer currentFrameNum) {
        this.frameCount = currentFrameNum;
    }
}
