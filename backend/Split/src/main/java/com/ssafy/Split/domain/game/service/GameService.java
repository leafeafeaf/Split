package com.ssafy.Split.domain.game.service;

import com.ssafy.Split.domain.bowling.domain.entity.Progress;
import com.ssafy.Split.domain.bowling.repository.FrameRepository;
import com.ssafy.Split.domain.bowling.repository.ProgressRepository;
import com.ssafy.Split.domain.game.domain.dto.request.GameUploadRequest;
import com.ssafy.Split.domain.game.domain.dto.response.GameListResponse;
import com.ssafy.Split.domain.game.domain.dto.response.GameResponse;
import com.ssafy.Split.domain.game.domain.entity.Game;
import com.ssafy.Split.domain.game.repository.GameRepository;
import com.ssafy.Split.domain.user.domain.entity.User;
import com.ssafy.Split.domain.user.repository.UserRepository;
import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Builder
@RequiredArgsConstructor
@Slf4j
public class GameService {

  private final GameRepository gameRepository;
  private final UserRepository userRepository;
  private final ProgressRepository progressRepository;
  private final FrameRepository frameRepository;

  @Transactional
  public Integer uploadGame(int userId, GameUploadRequest request) {

    // 사용자 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new SplitException(ErrorCode.USER_NOT_FOUND, String.valueOf(userId)));

    // 2. Progress와 Frame 데이터 삭제
    Progress progress = progressRepository.findByDeviceSerialNumber(request.getSerialNum())
        .orElseThrow(() -> new SplitException(ErrorCode.PROGRESS_NOT_FOUND,
            String.valueOf(request.getSerialNum())));

    // 프레임 삭제
    frameRepository.deleteAllByProgressId(progress.getId());
    // Progress 삭제
    progressRepository.delete(progress);

    // Game 생성
    Game game = Game.builder()
        .user(user)
        .gameDate(LocalDateTime.now())
        .isSkip(request.getIsSkip() == 1)
        .poseHighscore(request.getPoseHighscore())
        .poseLowscore(request.getPoseLowscore())
        .poseAvgscore(request.getPoseAvgscore())
        .elbowAngleScore(request.getElbowAngleScore())
        .armStabilityScore(request.getArmStabilityScore())
        .armSpeed(request.getArmSpeed())
        .bowlingScore(request.getBowlingScore())
        .build();

    Game savedGame = gameRepository.save(game);

    // 4. 사용자 정보 업데이트
    updateUserStats(user, game);

    log.info("Game uploaded for user {}: {}", request.getUserId(), savedGame);

    return savedGame.getId();
  }

  protected void updateUserStats(User user, Game game) {
    // 평균 계산이 필요한 항목들 업데이트
    user.setTotalPoseAvgscore(calculateNewAverage(
        user.getTotalPoseAvgscore(),
        game.getPoseAvgscore(),
        user.getTotalGameCount()
    ));

    user.setElbowAngleScore(calculateNewAverage(
        user.getElbowAngleScore(),
        game.getElbowAngleScore(),
        user.getTotalGameCount()
    ));

    user.setArmStabilityScore(calculateNewAverage(
        user.getArmStabilityScore(),
        game.getArmStabilityScore(),
        user.getTotalGameCount()
    ));

    user.setArmSpeedScore(calculateNewAverage(
        user.getArmSpeedScore(),
        game.getArmSpeed(),
        user.getTotalGameCount()
    ));

    user.setAvgBowlingScore(calculateIntegerAverage(
        user.getAvgBowlingScore(),
        game.getBowlingScore()
    ));

    // BigDecimal 타입의 최고 포즈 점수 비교
    BigDecimal currentHighscore = user.getTotalPoseHighscore();
    BigDecimal newHighscore = game.getPoseHighscore();
    user.setTotalPoseHighscore(currentHighscore.max(newHighscore));

    // 현재 볼링 점수는 항상 최신 게임 점수로 갱신
    user.setCurrBowlingScore(game.getBowlingScore());

    // 게임 카운트 증가
    user.increaseTotalGameCount();

    // 변경된 사용자 정보 저장
    userRepository.save(user);
  }

  // Integer 타입 평균 계산
  private int calculateIntegerAverage(int currentAvg, int newValue) {
    return (currentAvg + newValue) / 2;  // 단순히 두 값의 평균을 계산
  }

  // BigDecimal 타입 평균 계산 (다른 BigDecimal 필드들용)
  private BigDecimal calculateNewAverage(BigDecimal currentAvg, BigDecimal newValue,
      int currentCount) {
    return currentAvg.multiply(BigDecimal.valueOf(currentCount))
        .add(newValue)
        .divide(BigDecimal.valueOf(currentCount + 1), 2, RoundingMode.HALF_DOWN);
  }


  public GameResponse.GameData getGame(int userId, Integer gameId) {
    Game game = gameRepository.findById(gameId)
        .orElseThrow(() -> new SplitException(ErrorCode.GAME_NOT_FOUND, String.valueOf(gameId)));

    // 사용자 권한 검증
    if (!game.getUser().getId().equals(userId)) {
      throw new SplitException(ErrorCode.USER_MISMATCH);
    }

    return GameResponse.GameData.builder()
        .id(game.getId())
        .userId(game.getUser().getId())
        .gameDate(game.getGameDate().toString())
        .isSkip(game.getIsSkip() ? 1 : 0)
        .poseHighscore(game.getPoseHighscore())
        .poseLowscore(game.getPoseLowscore())
        .poseAvgscore(game.getPoseAvgscore())
        .elbowAngleScore(game.getElbowAngleScore())
        .armStabilityScore(game.getArmStabilityScore())
        .armSpeed(game.getArmSpeed())
        .bowlingScore(game.getBowlingScore())
        .build();
  }

  public GameListResponse.GameListData getGameList(int userId, Integer count) {
    List<Game> games = count == null ?
        gameRepository.findByUserIdOrderByGameDateDesc(userId) :
        gameRepository.findTopNByUserIdOrderByGameDateDesc(userId, count);

    if (games.isEmpty()) {
      throw new SplitException(ErrorCode.GAME_ALREADY_DELETED);
    }

    List<GameListResponse.GameDetail> gameDetails = games.stream()
        .map(game -> GameListResponse.GameDetail.builder()
            .id(game.getId())
            .userId(game.getUser().getId())
            .gameDate(game.getGameDate().toString())
            .isSkip(game.getIsSkip() ? 1 : 0)
            .poseHighscore(game.getPoseHighscore())
            .poseLowscore(game.getPoseLowscore())
            .poseAvgscore(game.getPoseAvgscore())
            .elbowAngleScore(game.getElbowAngleScore())
            .armStabilityScore(game.getArmStabilityScore())
            .armSpeed(game.getArmSpeed())
            .build())
        .collect(Collectors.toList());

    return GameListResponse.GameListData.builder()
        .count(gameDetails.size())
        .gameArr(gameDetails)
        .build();
  }
}

