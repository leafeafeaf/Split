package com.ssafy.Split.global.common.batch.tasklet;

import com.ssafy.Split.domain.game.domain.entity.Game;
import com.ssafy.Split.domain.game.repository.GameRepository;
import com.ssafy.Split.domain.rank.domain.entity.Rank;
import com.ssafy.Split.domain.rank.repository.RankRepository;
import com.ssafy.Split.domain.user.domain.entity.User;
import com.ssafy.Split.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RankInsertTasklet implements Tasklet {

  private final GameRepository gameRepository;
  private final RankRepository rankRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

    LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

    // 먼저 조건을 만족하는 유저 수 확인
//    log.info("조건을 만족하는 유저 수: {}", qualifiedUsers.size());

    // 단계별 데이터 확인
    long totalGames = gameRepository.countGamesInLastYear(oneYearAgo);
    log.info("1년 내 전체 게임 수: {}", totalGames);

    long notSkippedGames = gameRepository.countNotSkippedGamesInLastYear(oneYearAgo);
    log.info("1년 내 스킵하지 않은 게임 수: {}", notSkippedGames);

    List<Object[]> usersWithFiveGames = gameRepository.countUsersWithFiveOrMoreGames(oneYearAgo);
    log.info("5게임 이상 플레이한 유저 수: {}", usersWithFiveGames.size());

    // 기존 로직
    List<Game> topGames = gameRepository.findTopRankedGames(oneYearAgo);
    log.info("조회된 게임 수: {}", topGames.size());

    topGames.forEach(game -> {
      try {
        Game existingGame = gameRepository.findById(game.getId()).orElseThrow();
        User existingUser = userRepository.findById(game.getUser().getId()).orElseThrow();

        Rank rank = Rank.builder()
            .nickname(game.getUser().getNickname())
            .highlight(game.getUser().getHighlight())
            .totalGameCount(game.getUser().getTotalGameCount())
            .gameDate(game.getGameDate())
            .poseHighscore(game.getPoseHighscore())
            .poseLosescore(game.getPoseLowscore())
            .poseAvgscore(game.getPoseAvgscore())
            .elbowAngleScore(game.getElbowAngleScore())
            .armStabilityScore(game.getArmStabilityScore())
            .armSpeed(game.getArmSpeed())
            .build();
        log.info("00000000000000000랭크 저장 완료 - 게임ID: {}, 유저ID: {}", game.getId(),
            game.getUser().getId());

        rankRepository.save(rank);
        log.info("111111111111111111랭크 저장 완료 - 게임ID: {}, 유저ID: {}", game.getId(),
            game.getUser().getId());

        gameRepository.save(existingGame);
        log.info("222222222222222222랭크 저장 완료 - 게임ID: {}, 유저ID: {}", game.getId(),
            game.getUser().getId());
      } catch (Exception e) {
        log.error("랭크 저장 실패 - 게임ID: {}, 에러: {}", game.getId(), e.getMessage(), e);
      }
    });

    long rankCount = rankRepository.count();
    log.info("최종 저장된 랭크 수: {}", rankCount);

    return RepeatStatus.FINISHED;
  }
}
