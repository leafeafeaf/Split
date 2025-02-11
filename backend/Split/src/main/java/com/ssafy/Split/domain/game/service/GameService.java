package com.ssafy.Split.domain.game.service;

import com.ssafy.Split.domain.game.domain.dto.request.GameUploadRequest;
import com.ssafy.Split.domain.game.domain.dto.response.GameListResponse;
import com.ssafy.Split.domain.game.domain.dto.response.GameResponse;
import com.ssafy.Split.domain.game.domain.entity.Game;
import com.ssafy.Split.domain.game.repository.GameRepository;
import com.ssafy.Split.domain.user.domain.entity.User;
import com.ssafy.Split.domain.user.repository.UserRepository;
import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;
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

  @Transactional
  public Integer uploadGame(int userId, GameUploadRequest request) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new SplitException(ErrorCode.USER_NOT_FOUND, String.valueOf(userId)));

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
        .build();

    Game savedGame = gameRepository.save(game);
    log.info("Game uploaded for user {}: {}", request.getUserId(), savedGame);

    return savedGame.getId();
  }

  public GameResponse.GameData getGame(int userId, Integer gameId) {

    Game game = gameRepository.findById(gameId)
        .orElseThrow(() -> new SplitException(ErrorCode.GAME_NOT_FOUND, String.valueOf(gameId)));

    // 사용자가 본인 게임만 조회할 수 있도록 검증
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

