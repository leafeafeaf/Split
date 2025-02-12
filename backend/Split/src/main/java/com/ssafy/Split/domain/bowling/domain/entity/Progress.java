package com.ssafy.Split.domain.bowling.domain.entity;

import com.ssafy.Split.domain.user.domain.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
  @OnDelete(action = OnDeleteAction.CASCADE)  // DB 레벨에서 User 삭제 시 Progress 삭제
  private User user;

  @Column(name = "frame_count", nullable = false)
  private Integer frameCount;

  @Column(nullable = false)
  private LocalDateTime time;

  @OneToMany(mappedBy = "progress", cascade = CascadeType.ALL, orphanRemoval = true)
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
