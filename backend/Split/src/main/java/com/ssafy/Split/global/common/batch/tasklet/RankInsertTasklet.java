package com.ssafy.Split.global.common.batch.tasklet;

import com.ssafy.Split.domain.game.repository.GameRepository;
import java.time.LocalDateTime;
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

  @Override
  @Transactional
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

    LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
    gameRepository.insertTopRankedGames(oneYearAgo);
    return RepeatStatus.FINISHED;
  }
}
