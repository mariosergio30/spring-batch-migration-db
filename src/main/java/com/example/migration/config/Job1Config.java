package com.example.migration.config;

import com.example.migration.batch.processor.OrderProcessor;
import com.example.migration.batch.reader.MongoOrderReader;
import com.example.migration.batch.writer.OracleOrderWriter;
import com.example.migration.domain.mongo.OrderDocument;
import com.example.migration.domain.oracle.OrderEntity;
import com.example.migration.listener.Job1ExecutionListener;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;

/**
 * Defines Job 1: mongoToOracleJob
 *
 * Flow: MongoOrderReader → OrderProcessor → OracleOrderWriter
 *
 * The step uses a TaskExecutor so multiple threads process chunks concurrently.
 * throttleLimit caps the active thread count to prevent DB connection exhaustion.
 *
 * To add Job 2, create Job2Config.java following this same structure.
 */
@Configuration
public class Job1Config {

    @Value("${migration.job1.chunk-size:100}")
    private int chunkSize;

    @Bean
    public Step job1Step(
            JobRepository jobRepository,
            MongoOrderReader reader,
            OrderProcessor processor,
            OracleOrderWriter writer,
            AsyncTaskExecutor migrationTaskExecutor) {

        return new StepBuilder("job1Step", jobRepository)
                .<OrderDocument, OrderEntity>chunk(chunkSize)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(migrationTaskExecutor)
                .build();
    }

    @Bean
    public Job mongoToOracleJob(
            JobRepository jobRepository,
            Step job1Step,
            Job1ExecutionListener listener) {

        return new JobBuilder("mongoToOracleJob", jobRepository)
                .incrementer(new RunIdIncrementer())   // re-runnable without parameter changes
                .listener(listener)
                .start(job1Step)
                .build();
    }
}
