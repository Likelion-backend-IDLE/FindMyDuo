package com.idle.fmd.domain.lol.job;

import com.idle.fmd.domain.lol.dto.LolAccountResponseDto;
import com.idle.fmd.domain.lol.entity.LolAccountEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@EnableBatchProcessing
public class LolApiJob {
    private final PlatformTransactionManager transactionManager;
    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final LolItemReader itemReader;
    private final LolItemProcessor itemProcessor;
    private final LolItemWriter itemWriter;

    // 주기적으로 잡을 실행하기 위한 스케줄러 설정
    // 나중에 예외처리 할 것
    @Scheduled(fixedDelay = 1800000) // 30분마다 실행
    public void runJob() throws Exception {
        JobExecution jobExecution = jobLauncher.run(lolAccountJob(), new JobParameters());
        // 잡을 실행했을 때 성공적이면 로그에 성공 출력, 실패하면 실패 출력
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("롤 APi Job을 실행하는데 성공하였습니다.");
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.info("Job을 실행하는데 실패했습니다.");
        }
    }

    @Bean
    public Job lolAccountJob() {
        return new JobBuilder("lolAccountJob", jobRepository)
                // 여러번 호출할 수 있도록 중복되지 않게 실행
                .incrementer(new RunIdIncrementer())
                // 라이엇 API 를 이용하여 DB에 동기화하는 step 실행
                .start(lolAccountStep())
                .build();
    }

    @Bean
    public Step lolAccountStep() {
        return new StepBuilder("lolAccountStep", jobRepository)
                .<LolAccountResponseDto, LolAccountEntity>chunk(1)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                // 트랜잭션 매니저 설정
                .transactionManager(transactionManager)
                // 이미 완료된 스텝을 다시 시작할 수 있도록 설정
                .allowStartIfComplete(true)
                .build();
    }
}