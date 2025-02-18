package com.ssafy.Split.domain.game.service;


import com.ssafy.Split.domain.game.domain.entity.Game;
import com.ssafy.Split.domain.user.domain.entity.User;
import jakarta.persistence.EntityManager;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class ComprehensivePerformanceTest {

  @BeforeEach
  public void cleanupDatabase() {
    entityManager.createNativeQuery("DELETE FROM game").executeUpdate();
    entityManager.createNativeQuery("DELETE FROM user").executeUpdate();
    entityManager.flush();
  }


  @Autowired
  private GameRankComparisonService comparisonService;

  @Autowired
  private EntityManager entityManager;

//  private static final int MAX_USERS = 10;

  @Test
  public void testWithVariousDataSizes() throws Exception {
    int[] dataSizes = {10, 20}; // 필요에 따라 조정
    int repetitions = 3; // 각 크기별 반복 횟수

    Map<Integer, List<Long>> nonBatchResults = new HashMap<>();
    Map<Integer, List<Long>> batchResults = new HashMap<>();

    for (int size : dataSizes) {
      nonBatchResults.put(size, new ArrayList<>());
      batchResults.put(size, new ArrayList<>());

      for (int i = 0; i < repetitions; i++) {
        System.out.println("===== 데이터 크기: " + size + ", 반복: " + (i + 1) + " =====");

        // 배치 미사용 테스트
        prepareTestData(size);
        entityManager.clear();
        System.gc();

        long start1 = System.currentTimeMillis();
        comparisonService.executeRankingWithoutBatch();
        long end1 = System.currentTimeMillis();
        long duration1 = end1 - start1;
        nonBatchResults.get(size).add(duration1);

        // 테스트 데이터 재설정
        resetData();

        // 배치 사용 테스트
        prepareTestData(size);
        entityManager.clear();
        System.gc();

        long start2 = System.currentTimeMillis();
        comparisonService.executeRankingWithBatch();
        long end2 = System.currentTimeMillis();
        long duration2 = end2 - start2;
        batchResults.get(size).add(duration2);

        // 테스트 데이터 정리
        resetData();
      }
    }

    // 평균 계산 및 결과 저장
    saveComprehensiveResults(dataSizes, nonBatchResults, batchResults);
  }

  private void saveComprehensiveResults(int[] dataSizes,
      Map<Integer, List<Long>> nonBatchResults,
      Map<Integer, List<Long>> batchResults) {
    try (FileWriter writer = new FileWriter("comprehensive_results.csv")) {
      writer.write("데이터크기,배치미사용평균(ms),배치사용평균(ms),성능향상비율\n");

      for (int size : dataSizes) {
        double avgNonBatch = nonBatchResults.get(size).stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0);

        double avgBatch = batchResults.get(size).stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0);

        double ratio = avgNonBatch / avgBatch;

        writer.write(String.format("%d,%.2f,%.2f,%.2f\n",
            size, avgNonBatch, avgBatch, ratio));

        System.out.println("===== 결과 요약: 데이터 크기 " + size + " =====");
        System.out.println("배치 미사용 평균 시간: " + avgNonBatch + "ms");
        System.out.println("배치 사용 평균 시간: " + avgBatch + "ms");
        System.out.println("성능 향상 비율: " + ratio + "배");
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Transactional
  protected void prepareTestData(int count) {
    // 필요한 테스트 데이터 생성
    // 예: JPA를 사용하여 Game과 User 엔티티 생성
    Random random = new Random();

    for (int i = 0; i < count / 10; i++) { // 10:1 비율로 사용자:게임 생성
      User user = new User();
      user.setNickname("User" + i);
      user.setEmail("user" + i + "@google.com");
      user.setPassword("password" + i);
      user.setTotalGameCount(random.nextInt(100));
      entityManager.persist(user);

      for (int j = 0; j < 10; j++) {
        Game game = new Game();
        game.setUser(user);
        game.setGameDate(LocalDateTime.now().minusDays(random.nextInt(365)));
        game.setPoseHighscore(
            BigDecimal.valueOf(random.nextDouble() * 100).setScale(2, RoundingMode.HALF_UP));
        game.setPoseLowscore(
            BigDecimal.valueOf(random.nextDouble() * 50).setScale(2, RoundingMode.HALF_UP));
        game.setPoseAvgscore(
            BigDecimal.valueOf(random.nextDouble() * 80).setScale(2, RoundingMode.HALF_UP));
        game.setElbowAngleScore(
            BigDecimal.valueOf(random.nextDouble() * 90).setScale(2, RoundingMode.HALF_UP));
        game.setArmStabilityScore(
            BigDecimal.valueOf(random.nextDouble() * 85).setScale(2, RoundingMode.HALF_UP));
        game.setArmSpeed(
            BigDecimal.valueOf(random.nextDouble() * 70).setScale(2, RoundingMode.HALF_UP));
        game.setIsSkip(random.nextBoolean());
        entityManager.persist(game);
      }

      // 메모리 관리를 위해 주기적으로 flush 및 clear
      if (i % 100 == 0) {
        entityManager.flush();
        entityManager.clear();
      }
    }

    entityManager.flush();
  }

  @Transactional
  protected void resetData() {
    // 테스트 테이블 초기화
    entityManager.createNativeQuery("DELETE FROM game_rank").executeUpdate();
    entityManager.createNativeQuery("DELETE FROM game").executeUpdate();
    entityManager.createNativeQuery("DELETE FROM user").executeUpdate();

    entityManager.flush();
  }

}
