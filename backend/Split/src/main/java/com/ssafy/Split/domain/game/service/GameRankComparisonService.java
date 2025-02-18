package com.ssafy.Split.domain.game.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Service
public class GameRankComparisonService {

  @PersistenceContext
  private EntityManager entityManager;

  // 배치 없이 JPA/JPQL로 직접 구현
  @Transactional
  public void executeRankingWithoutBatch() {
    StopWatch stopWatch = new StopWatch("배치 미사용 랭킹 처리");
    stopWatch.start();

    LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

    // JPQL 또는 네이티브 쿼리 사용
    Query query = entityManager.createNativeQuery(
        "INSERT INTO game_rank (game_id, user_id, nickname, total_game_count, game_date, pose_highscore, pose_lowscore, pose_avgscore, elbow_angle_score, arm_stability_score, arm_speed) "
            +
            "SELECT g.id, g.user_id, u.nickname, u.total_game_count, g.game_date, g.pose_highscore, g.pose_lowscore, g.pose_avgscore, g.elbow_angle_score, g.arm_stability_score, g.arm_speed "
            +
            "FROM game g JOIN user u ON g.user_id = u.id " +
            "WHERE g.user_id IN ( " +
            "    SELECT g2.user_id FROM game g2 WHERE g2.game_date >= :oneYearAgo AND g2.is_skip = false "
            +
            "    GROUP BY g2.user_id HAVING COUNT(g2.user_id) >= 5 " +
            ") " +
            "AND g.pose_avgscore = ( " +
            "    SELECT MAX(g3.pose_avgscore) FROM game g3 WHERE g3.user_id = g.user_id " +
            ") " +
            "ORDER BY g.pose_avgscore DESC"
    );

    query.setParameter("oneYearAgo", oneYearAgo);
    int updatedCount = query.executeUpdate();

    stopWatch.stop();
    System.out.println("배치 미사용 실행 시간: " + stopWatch.getTotalTimeMillis() + "ms");
    System.out.println("삽입된 레코드 수: " + updatedCount);
  }

  // 배치 사용 버전
  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private Job rankingJob;

  public void executeRankingWithBatch() {
    StopWatch stopWatch = new StopWatch("배치 사용 랭킹 처리");
    stopWatch.start();

    JobParameters parameters = new JobParametersBuilder()
        .addString("execution_id", UUID.randomUUID().toString())
        .addDate("execution_date", new Date())
        .toJobParameters();

    try {
      JobExecution execution = jobLauncher.run(rankingJob, parameters);
      System.out.println("배치 작업 상태: " + execution.getStatus());
      System.out.println("처리된 레코드 수: " + execution.getStepExecutions().stream()
          .mapToLong(StepExecution::getWriteCount)
          .sum());
    } catch (Exception e) {
      e.printStackTrace();
    }

    stopWatch.stop();
    System.out.println("배치 사용 실행 시간: " + stopWatch.getTotalTimeMillis() + "ms");
  }
}