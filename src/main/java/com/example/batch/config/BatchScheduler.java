package com.example.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job testjob;


    @Scheduled(cron = "0 * * * * *") // 매 1분마다(매 분 0초에) 실행
    public void runJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis()) // 매 실행마다 고유한 파라미터 생성
                    .toJobParameters();

            jobLauncher.run(testjob, jobParameters); // testjob 배치 실행
            log.info("스케줄러로 testjob 실행"); // 실행 로그 출력
        } catch (Exception e) {
            log.error("배치 실행 실패", e); // 실행 중 예외 발생 시 에러 로그 출력
        }
    }
}
