package com.ssafy.Split.game.service;

import com.ssafy.Split.game.domain.dto.request.GameUploadRequest;
import com.ssafy.Split.game.domain.entity.Game;
import com.ssafy.Split.game.repository.GameRepository;
import com.ssafy.Split.user.domain.entity.User;
import com.ssafy.Split.user.exception.UserNotFoundException;
import com.ssafy.Split.user.repository.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
}
