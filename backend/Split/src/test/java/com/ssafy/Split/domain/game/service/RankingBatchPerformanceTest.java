package com.ssafy.Split.domain.game.service;

import com.ssafy.Split.domain.game.domain.entity.Game;
import com.ssafy.Split.domain.user.domain.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RankingBatchPerformanceTest {

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private GameRankComparisonService comparisonService;

  // 테스트 데이터 크기 설정
  private static final int TOTAL_USERS = 100000;   // 총 사용자 수
  private static final int GAMES_PER_USER = 10;  // 사용자 당 게임 수
  private static final int TOP_RANKS = 5000;     // 저장할 상위 랭킹 수

  @BeforeEach
  public void cleanupDatabase() {
    entityManager.createNativeQuery("DELETE FROM game_rank").executeUpdate();
    entityManager.createNativeQuery("DELETE FROM game").executeUpdate();
    entityManager.createNativeQuery("DELETE FROM user").executeUpdate();
    entityManager.flush();
  }

  @Test
  public void comparePerformanceWithAndWithoutBatch() throws Exception {
    // 1. 테스트 데이터 생성
    System.out.println("테스트 데이터 생성 중...");
    prepareTestData(TOTAL_USERS, GAMES_PER_USER);
    System.out.println(
        "테스트 데이터 생성 완료: 사용자 " + TOTAL_USERS + "명, 게임 " + (TOTAL_USERS * GAMES_PER_USER) + "개");

    // 2. 배치 처리 없이 직접 쿼리 성능 측정 (모든 게임 데이터 조회)
    System.out.println("배치 처리 없이 전체 데이터 쿼리 성능 측정 시작...");
    long startWithoutBatch = System.currentTimeMillis();
    List<?> resultWithoutBatch = queryTopRanksWithoutBatch();
    long endWithoutBatch = System.currentTimeMillis();
    long durationWithoutBatch = endWithoutBatch - startWithoutBatch;
    System.out.println("배치 처리 없이 전체 데이터 쿼리 소요 시간: " + durationWithoutBatch + "ms");
    System.out.println("조회된 결과 수: " + resultWithoutBatch.size());

    // 3. 배치 작업 실행 (상위 5,000개 데이터만 game_rank 테이블에 저장)
    System.out.println("배치 작업 실행 중...");
    long batchStart = System.currentTimeMillis();
    comparisonService.executeRankingWithBatch();
    long batchEnd = System.currentTimeMillis();
    System.out.println("배치 작업 소요 시간: " + (batchEnd - batchStart) + "ms");

    // 4. 배치 처리 후 game_rank 테이블 조회 성능 측정
    System.out.println("배치 처리 후 game_rank 테이블 쿼리 성능 측정 시작...");
    long startWithBatch = System.currentTimeMillis();
    List<?> resultWithBatch = queryGameRankTable();
    long endWithBatch = System.currentTimeMillis();
    long durationWithBatch = endWithBatch - startWithBatch;
    System.out.println("배치 처리 후 game_rank 테이블 쿼리 소요 시간: " + durationWithBatch + "ms");
    System.out.println("조회된 결과 수: " + resultWithBatch.size());

    // 5. 결과 저장 및 비교
    saveResults(durationWithoutBatch, durationWithBatch);

    // 성능 향상 비율 계산
    double improvementRatio = (double) durationWithoutBatch / durationWithBatch;
    System.out.println("=== 성능 테스트 결과 요약 ===");
    System.out.println("배치 처리 없이 조회 시간: " + durationWithoutBatch + "ms");
    System.out.println("배치 처리 후 조회 시간: " + durationWithBatch + "ms");
    System.out.println("성능 향상 비율: " + String.format("%.2f", improvementRatio) + "배 더 빠름");
  }

  /**
   * 테스트 데이터 생성 - User와 Game 엔티티에 맞춰 조정됨
   */
  @Transactional
  protected void prepareTestData(int userCount, int gamesPerUser) {
    Random random = new Random();

    System.out.println("사용자 및 게임 데이터 생성 중...");
    for (int i = 0; i < userCount; i++) {
      User user = User.builder()
          .email("user" + i + "@example.com")
          .password("password" + i)
          .nickname("User" + i)
          .gender(random.nextInt(3) + 1) // 1: 남자, 2: 여자, 3: 기타/비공개
          .height(160 + random.nextInt(40)) // 160-200cm
          .totalGameCount(gamesPerUser)
          .totalPoseHighscore(
              BigDecimal.valueOf(random.nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
          .totalPoseAvgscore(
              BigDecimal.valueOf(random.nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
          .elbowAngleScore(
              BigDecimal.valueOf(random.nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
          .armStabilityScore(
              BigDecimal.valueOf(random.nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
          .armSpeedScore(
              BigDecimal.valueOf(random.nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
          .thema(random.nextInt(2) + 1) // 1: 라이트, 2: 다크
          .currBowlingScore(random.nextInt(300))
          .avgBowlingScore(random.nextInt(300))
          .build();

      entityManager.persist(user);

      // 각 사용자별 게임 데이터 생성
      for (int j = 0; j < gamesPerUser; j++) {
        Game game = Game.builder()
            .user(user)
            .gameDate(LocalDateTime.now().minusDays(random.nextInt(365)))
            .isSkip(random.nextInt(10) < 1) // 90% 확률로 false
            .poseHighscore(
                BigDecimal.valueOf(random.nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
            .poseLowscore(
                BigDecimal.valueOf(random.nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
            .poseAvgscore(
                BigDecimal.valueOf(random.nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
            .elbowAngleScore(
                BigDecimal.valueOf(random.nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
            .armStabilityScore(
                BigDecimal.valueOf(random.nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
            .armSpeed(
                BigDecimal.valueOf(random.nextDouble() * 100).setScale(2, RoundingMode.HALF_UP))
            .bowlingScore(random.nextInt(300))
            .build();

        entityManager.persist(game);
      }

      // 메모리 관리를 위해 주기적으로 flush 및 clear
      if (i % 100 == 0) {
        entityManager.flush();
        entityManager.clear();
        System.out.println("데이터 생성 진행 중: " + i + "/" + userCount + " 사용자 처리됨");
      }
    }

    entityManager.flush();
    System.out.println("데이터 생성 완료: 총 " + userCount + " 사용자, " + (userCount * gamesPerUser) + " 게임");
  }

  /**
   * 배치 처리 없이 직접 모든 데이터에서 top 랭킹 조회
   */
  @SuppressWarnings("unchecked")
  private List<?> queryTopRanksWithoutBatch() {
    LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

    String query = "SELECT g.id as game_id, g.user_id, u.nickname, u.total_game_count, " +
        "g.game_date, g.pose_highscore, g.pose_lowscore, g.pose_avgscore, " +
        "g.elbow_angle_score, g.arm_stability_score, g.arm_speed, g.bowling_score " +
        "FROM game g JOIN user u ON g.user_id = u.id " +
        "WHERE g.user_id IN ( " +
        "    SELECT g2.user_id FROM game g2 WHERE g2.game_date >= :oneYearAgo AND g2.is_skip = false "
        +
        "    GROUP BY g2.user_id HAVING COUNT(g2.user_id) >= 5 " +
        ") " +
        "AND g.pose_avgscore = ( " +
        "    SELECT MAX(g3.pose_avgscore) FROM game g3 WHERE g3.user_id = g.user_id " +
        ") " +
        "ORDER BY g.pose_avgscore DESC";

    return entityManager.createNativeQuery(query)
        .setParameter("oneYearAgo", oneYearAgo)
        .setMaxResults(TOP_RANKS)
        .getResultList();
  }

  /**
   * 배치 처리 후 game_rank 테이블에서 조회
   */
  @SuppressWarnings("unchecked")
  private List<?> queryGameRankTable() {
    String query = "SELECT * FROM game_rank ORDER BY pose_avgscore DESC";
    return entityManager.createNativeQuery(query).getResultList();
  }

  /**
   * 테스트 결과 파일로 저장
   */
  private void saveResults(long durationWithoutBatch, long durationWithBatch) {
    try (FileWriter writer = new FileWriter("ranking_performance_test_results.csv", true)) {
      // 파일이 없으면 헤더 추가
      if (new java.io.File("ranking_performance_test_results.csv").length() == 0) {
        writer.write("테스트일시,총사용자수,사용자당게임수,전체데이터수,배치없이조회시간(ms),배치후조회시간(ms),성능향상비율\n");
      }

      double ratio = (double) durationWithoutBatch / durationWithBatch;
      int totalGames = TOTAL_USERS * GAMES_PER_USER;

      writer.write(String.format("%s,%d,%d,%d,%d,%d,%.2f\n",
          LocalDateTime.now(),
          TOTAL_USERS,
          GAMES_PER_USER,
          totalGames,
          durationWithoutBatch,
          durationWithBatch,
          ratio));

      System.out.println("테스트 결과가 ranking_performance_test_results.csv 파일에 저장되었습니다.");

    } catch (IOException e) {
      System.err.println("결과 저장 중 오류 발생: " + e.getMessage());
      e.printStackTrace();
    }
  }
}