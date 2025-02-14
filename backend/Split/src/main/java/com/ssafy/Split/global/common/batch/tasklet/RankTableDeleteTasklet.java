package com.ssafy.Split.global.common.batch.tasklet;

import com.ssafy.Split.domain.rank.repository.RankRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RankTableDeleteTasklet implements Tasklet {

  private final RankRepository rankRepository;

  public RankTableDeleteTasklet(RankRepository rankRepository) {
    this.rankRepository = rankRepository;
  }

  @Override
  @Transactional
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    rankRepository.deleteAll();
    return RepeatStatus.FINISHED;
  }
}
