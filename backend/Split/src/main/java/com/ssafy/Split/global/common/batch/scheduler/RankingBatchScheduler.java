package com.ssafy.Split.global.common.batch.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class RankingBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job rankUpdateJob;

    @Scheduled(cron = "0 * * * * *")  // 1분마다 실행
    public void runRankingJob() {
        log.info("#########################################1분");
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(rankUpdateJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("끝");
    }
}



