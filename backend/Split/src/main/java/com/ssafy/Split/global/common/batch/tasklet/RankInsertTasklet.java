package com.ssafy.Split.global.common.batch.tasklet;

import com.ssafy.Split.domain.game.domain.entity.Game;
import com.ssafy.Split.domain.game.repository.GameRepository;
import com.ssafy.Split.domain.rank.domain.entity.Rank;
import com.ssafy.Split.domain.rank.repository.RankRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RankInsertTasklet implements Tasklet {

  private final GameRepository gameRepository;
  private final RankRepository rankRepository;

  public RankInsertTasklet(GameRepository gameRepository, RankRepository rankRepository) {
    this.gameRepository = gameRepository;
    this.rankRepository = rankRepository;
  }

  @Override
  @Transactional
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    List<Game> topGames = gameRepository.findTopRankedGames(LocalDateTime.now());

    topGames.forEach(game -> {
      Rank rank = Rank.builder()
          .game(game)
          .user(game.getUser())
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

      rankRepository.save(rank);
    });

    return RepeatStatus.FINISHED;
  }
}
