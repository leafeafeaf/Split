package com.ssafy.Split.domain.game.service;

import com.ssafy.Split.domain.game.domain.dto.request.GameUploadRequest;
import com.ssafy.Split.domain.game.domain.dto.response.GameListResponse;
import com.ssafy.Split.domain.game.domain.dto.response.GameResponse;
import com.ssafy.Split.domain.game.domain.entity.Game;
import com.ssafy.Split.domain.game.repository.GameRepository;
import com.ssafy.Split.domain.user.domain.entity.User;
import com.ssafy.Split.domain.user.exception.UserNotFoundException;
import com.ssafy.Split.domain.user.repository.UserRepository;
import com.ssafy.Split.global.common.exception.ErrorCode;
import com.ssafy.Split.global.common.exception.SplitException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Builder
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    @Transactional
    public Integer uploadGame(GameUploadRequest request) {

    // User 조회
    User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));

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
    public GameResponse getGame(Integer gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new SplitException(ErrorCode.GAME_NOT_FOUND, String.valueOf(gameId)));

        GameResponse.GameData gameData = GameResponse.GameData.builder()
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

        return GameResponse.builder()
                .code("SUCCESS")
                .status(200)
                .message("get Game data successfully")
                .data(gameData)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    public GameListResponse getGameList(int userId, Integer count) {
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

        GameListResponse.GameListData listData = GameListResponse.GameListData.builder()
                .count(gameDetails.size())
                .gameArr(gameDetails)
                .build();

        return GameListResponse.builder()
                .code("SUCCESS")
                .status(200)
                .message("Game list retrieved successfully")
                .data(listData)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}

