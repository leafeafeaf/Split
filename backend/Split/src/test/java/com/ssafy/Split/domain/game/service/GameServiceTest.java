package com.ssafy.Split.domain.game.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ssafy.Split.domain.game.domain.entity.Game;
import com.ssafy.Split.domain.game.repository.GameRepository;
import com.ssafy.Split.domain.user.domain.entity.User;
import com.ssafy.Split.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

  @InjectMocks
  private GameService gameService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private GameRepository gameRepository;

  @Test
  @DisplayName("유저 통계 업데이트 테스트")
  void updateUserStatsTest() {
    // given
    User user = User.builder()
        .id(1)
        .totalGameCount(2)
        .avgBowlingScore(150)
        .totalPoseHighscore(new BigDecimal("85.5"))
        .totalPoseAvgscore(new BigDecimal("80.0"))
        .elbowAngleScore(new BigDecimal("75.5"))
        .armStabilityScore(new BigDecimal("70.0"))
        .armSpeedScore(new BigDecimal("65.5"))
        .build();

    Game game = Game.builder()
        .id(1)
        .bowlingScore(180)
        .poseHighscore(new BigDecimal("90.0"))
        .poseAvgscore(new BigDecimal("85.0"))
        .elbowAngleScore(new BigDecimal("80.0"))
        .armStabilityScore(new BigDecimal("75.0"))
        .armSpeed(new BigDecimal("70.0"))
        .build();

    // 테스트 전 값들 출력
    System.out.println("테스트 전:");
    System.out.println("User의 현재 최고점수: " + user.getTotalPoseHighscore());
    System.out.println("Game의 포즈 점수: " + game.getPoseHighscore());

    // when
    when(userRepository.save(any(User.class))).thenReturn(user);
    gameService.updateUserStats(user, game);

    // 테스트 후 값 출력
    System.out.println("\n테스트 후:");
    System.out.println("업데이트된 유저 최고점수: " + user.getTotalPoseHighscore());

    // 모든 값 출력해서 확인
    System.out.println("=== 업데이트 후 값 확인 ===");
    System.out.println("totalPoseAvgscore 기대값: 81.67, 실제값: " + user.getTotalPoseAvgscore()
        .setScale(2, RoundingMode.HALF_UP));
    System.out.println("elbowAngleScore 기대값: 77.0, 실제값: " + user.getElbowAngleScore()
        .setScale(2, RoundingMode.HALF_UP));
    System.out.println("armStabilityScore 기대값: 71.67, 실제값: " + user.getArmStabilityScore()
        .setScale(2, RoundingMode.HALF_UP));
    System.out.println("armSpeedScore 기대값: 67.0, 실제값: " + user.getArmSpeedScore()
        .setScale(2, RoundingMode.HALF_UP));

    // then
    // 기대값도 스케일 맞춰주기
    BigDecimal expectedAvgScore = new BigDecimal("81.67").setScale(2, RoundingMode.HALF_UP);
    BigDecimal expectedElbowScore = new BigDecimal("77.0").setScale(2, RoundingMode.HALF_UP);
    BigDecimal expectedStabilityScore = new BigDecimal("71.67").setScale(2, RoundingMode.HALF_UP);
    BigDecimal expectedSpeedScore = new BigDecimal("67.0").setScale(2, RoundingMode.HALF_UP);

    assertEquals(0,
        expectedAvgScore.compareTo(user.getTotalPoseAvgscore().setScale(2, RoundingMode.HALF_UP)),
        String.format("Expected: %s, but was: %s", expectedAvgScore,
            user.getTotalPoseAvgscore().setScale(2, RoundingMode.HALF_UP)));
    assertEquals(0,
        expectedElbowScore.compareTo(user.getElbowAngleScore().setScale(2, RoundingMode.HALF_UP)),
        String.format("Expected: %s, but was: %s", expectedElbowScore,
            user.getElbowAngleScore().setScale(2, RoundingMode.HALF_UP)));
    assertEquals(0, expectedStabilityScore.compareTo(
            user.getArmStabilityScore().setScale(2, RoundingMode.HALF_UP)),
        String.format("Expected: %s, but was: %s", expectedStabilityScore,
            user.getArmStabilityScore().setScale(2, RoundingMode.HALF_UP)));
    assertEquals(0,
        expectedSpeedScore.compareTo(user.getArmSpeedScore().setScale(2, RoundingMode.HALF_UP)),
        String.format("Expected: %s, but was: %s", expectedSpeedScore,
            user.getArmSpeedScore().setScale(2, RoundingMode.HALF_UP)));

    verify(userRepository, times(1)).save(user);
  }

  @Test
  @DisplayName("첫 게임 통계 업데이트 테스트")
  void updateUserStatsFirstGameTest() {
    // given
    User user = User.builder()
        .id(1)
        .totalGameCount(0)
        .avgBowlingScore(0)
        .totalPoseHighscore(BigDecimal.ZERO)
        .totalPoseAvgscore(BigDecimal.ZERO)
        .elbowAngleScore(BigDecimal.ZERO)
        .armStabilityScore(BigDecimal.ZERO)
        .armSpeedScore(BigDecimal.ZERO)
        .build();

    Game game = Game.builder()
        .id(1)
        .bowlingScore(180)
        .poseHighscore(new BigDecimal("90.0"))
        .poseAvgscore(new BigDecimal("85.0"))
        .elbowAngleScore(new BigDecimal("80.0"))
        .armStabilityScore(new BigDecimal("75.0"))
        .armSpeed(new BigDecimal("70.0"))
        .build();

    // 테스트 전 값들 출력
    System.out.println("테스트 전:");
    System.out.println("User의 현재 최고점수: " + user.getTotalPoseHighscore());
    System.out.println("Game의 포즈 점수: " + game.getPoseHighscore());
    // when
    when(userRepository.save(any(User.class))).thenReturn(user);
    gameService.updateUserStats(user, game);

    // 테스트 후 값 출력
    System.out.println("\n테스트 후:");
    System.out.println("업데이트된 유저 최고점수: " + user.getTotalPoseHighscore());

    // then
    assertEquals(1, user.getTotalGameCount());
    assertEquals(180, user.getCurrBowlingScore());
    assertEquals(180, user.getAvgBowlingScore());

    // BigDecimal 비교
    assertEquals(0, new BigDecimal("90.0").compareTo(user.getTotalPoseHighscore()));
    assertEquals(0, new BigDecimal("85.0").compareTo(user.getTotalPoseAvgscore()));
    assertEquals(0, new BigDecimal("80.0").compareTo(user.getElbowAngleScore()));
    assertEquals(0, new BigDecimal("75.0").compareTo(user.getArmStabilityScore()));
    assertEquals(0, new BigDecimal("70.0").compareTo(user.getArmSpeedScore()));

    verify(userRepository, times(1)).save(user);
  }

  @Test
  @DisplayName("볼링 점수 업데이트 테스트 (평균 및 현재 점수)")
  void updateBowlingScoreTest() {
    // given
    User user = User.builder()
        .totalGameCount(1)      // 1로 수정 (현재 점수 하나만 있음)
        .avgBowlingScore(150)   // 현재 평균 150점
        .currBowlingScore(160)  // 현재 최신 점수 160점
        .totalPoseHighscore(BigDecimal.ZERO)
        .totalPoseAvgscore(BigDecimal.ZERO)
        .elbowAngleScore(BigDecimal.ZERO)
        .armStabilityScore(BigDecimal.ZERO)
        .armSpeedScore(BigDecimal.ZERO)
        .build();

    Game game = Game.builder()
        .bowlingScore(180)      // 새로운 게임 180점
        .poseHighscore(new BigDecimal("90.0"))
        .poseAvgscore(new BigDecimal("85.0"))
        .elbowAngleScore(new BigDecimal("80.0"))
        .armStabilityScore(new BigDecimal("75.0"))
        .armSpeed(new BigDecimal("70.0"))
        .build();

    System.out.println("=== 볼링 점수 업데이트 전 ===");
    System.out.println("현재 평균 점수: " + user.getAvgBowlingScore());
    System.out.println("현재 최신 점수: " + user.getCurrBowlingScore());
    System.out.println("새 게임 점수: " + game.getBowlingScore());

    // when
    when(userRepository.save(any(User.class))).thenReturn(user);
    gameService.updateUserStats(user, game);

    // then
    System.out.println("=== 볼링 점수 업데이트 후 ===");
    System.out.println("새로운 평균: " + user.getAvgBowlingScore());
    System.out.println("새로운 현재 점수: " + user.getCurrBowlingScore());

    // 평균 점수 확인 (150 + 180) / 2 = 165
    assertEquals(165, user.getAvgBowlingScore(),
        String.format("Expected bowling average: 165, but was: %d", user.getAvgBowlingScore()));

    // 현재 점수는 항상 최신 게임 점수여야 함
    assertEquals(180, user.getCurrBowlingScore(),
        String.format("Expected current bowling score: 180, but was: %d",
            user.getCurrBowlingScore()));

    // 게임 카운트 확인
    assertEquals(2, user.getTotalGameCount(),
        String.format("Expected game count: 2, but was: %d", user.getTotalGameCount()));
  }


}