package com.example.migration.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Central Spring Batch infrastructure configuration.
 *
 * Provides a shared ThreadPoolTaskExecutor that every job step can inject.
 * Thread count is read from migration.job1.thread-count (defaults to 4).
 *
 * Keep thread-count ≤ spring.datasource.hikari.maximum-pool-size to avoid
 * connection starvation under load.
 */
@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfig {

    @Value("${migration.job1.thread-count:4}")
    private int threadCount;

    @Bean
    public TaskExecutor migrationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadCount);
        executor.setMaxPoolSize(threadCount);
        executor.setQueueCapacity(threadCount * 2);
        executor.setThreadNamePrefix("batch-worker-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}
