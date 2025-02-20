package com.ssafy.Split.global.common.batch.config;

import com.ssafy.Split.global.common.batch.tasklet.RankInsertTasklet;
import com.ssafy.Split.global.common.batch.tasklet.RankTableDeleteTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;

  private final RankTableDeleteTasklet rankTableDeleteTasklet;
  private final RankInsertTasklet rankInsertTasklet;

  public BatchConfig(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      RankTableDeleteTasklet rankTableDeleteTasklet,
      RankInsertTasklet rankInsertTasklet) {
    this.jobRepository = jobRepository;
    this.transactionManager = transactionManager;
    this.rankTableDeleteTasklet = rankTableDeleteTasklet;
    this.rankInsertTasklet = rankInsertTasklet;
  }

  @Bean
  public Job rankUpdateJob() {
    return new JobBuilder("rankUpdateJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(deleteRankTableStep())
        .next(insertRankTableStep())
        .build();
  }

  @Bean
  public Step deleteRankTableStep() {
    return new StepBuilder("deleteRankTableStep", jobRepository)
        .tasklet(rankTableDeleteTasklet, transactionManager)
        .build();
  }

  @Bean
  public Step insertRankTableStep() {
    return new StepBuilder("insertRankTableStep", jobRepository)
        .tasklet(rankInsertTasklet, transactionManager)
        .build();
  }
}
