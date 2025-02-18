package com.ssafy.Split.domain.game.service;

import com.ssafy.Split.domain.game.domain.entity.Game;
import com.ssafy.Split.domain.user.domain.entity.User;
import jakarta.persistence.EntityManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Random;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GameRankComparisonServiceTest {


  @Before("테스트 초기화")
  public void cleanupDatabase() {
    entityManager.createNativeQuery("DELETE FROM game").executeUpdate();
    entityManager.createNativeQuery("DELETE FROM user").executeUpdate();
    entityManager.flush();
  }

  @Autowired
  private GameRankComparisonService comparisonService;

  @Autowired
  private EntityManager entityManager;

  @Test
  @Transactional
  public void comparePerformance() throws Exception {
    // 테스트 데이터 준비
    prepareTestData(10000); // 1만건

    // 캐시 초기화 및 가비지 컬렉션 실행
    entityManager.clear();
    System.gc();

    // 1. 배치 미사용 버전 실행 및 측정
    System.out.println("=== 배치 미사용 버전 실행 ===");
    long start1 = System.currentTimeMillis();
    comparisonService.executeRankingWithoutBatch();
    long end1 = System.currentTimeMillis();
    long duration1 = end1 - start1;

    // 테스트 데이터 재설정
    resetData();
    prepareTestData(10000);

    // 캐시 초기화 및 가비지 컬렉션 실행
    entityManager.clear();
    System.gc();

    // 2. 배치 사용 버전 실행 및 측정
    System.out.println("=== 배치 사용 버전 실행 ===");
    long start2 = System.currentTimeMillis();
    comparisonService.executeRankingWithBatch();
    long end2 = System.currentTimeMillis();
    long duration2 = end2 - start2;

    // 결과 출력
    System.out.println("배치 미사용 소요시간: " + duration1 + "ms");
    System.out.println("배치 사용 소요시간: " + duration2 + "ms");
    System.out.println("성능 향상 비율: " + ((double) duration1 / duration2) + "배");

    // 측정 결과를 CSV나 Excel로 저장하면 차트 작성에 편리합니다
    saveResultsToFile(duration1, duration2);
  }

  @Transactional
  protected void prepareTestData(int count) {
    // 필요한 테스트 데이터 생성
    // 예: JPA를 사용하여 Game과 User 엔티티 생성
    Random random = new Random();

    for (int i = 0; i < count / 10; i++) { // 10:1 비율로 사용자:게임 생성
      User user = new User();
      user.setNickname("User" + i);
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

  private void saveResultsToFile(long duration1, long duration2) {
    try (FileWriter writer = new FileWriter("performance_results.csv", true)) {
      // CSV 헤더가 없으면 추가
      File file = new File("performance_results.csv");
      if (file.length() == 0) {
        writer.write("데이터수,배치미사용(ms),배치사용(ms),성능향상비율\n");
      }

      // 결과 저장
      writer.write(String.format("10000,%d,%d,%.2f\n",
          duration1, duration2, (double) duration1 / duration2));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}