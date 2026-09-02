package com.example.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Application entry point.
 *
 * The job runs on the cron expression defined by migration.schedule.cron
 * (default: every day at 02:00).
 *
 * To run a different job, override migration.schedule.job-name.
 *
 * Spring Batch auto-run on startup is disabled via:
 *   spring.batch.job.enabled=false
 */
@SpringBootApplication
public class MigrationApplication {

    private static final Logger log = LoggerFactory.getLogger(MigrationApplication.class);

    private final JobLauncher jobLauncher;
    private final ApplicationContext ctx;
    private final String jobName;

    public MigrationApplication(
            JobLauncher jobLauncher,
            ApplicationContext ctx,
            @org.springframework.beans.factory.annotation.Value("${migration.schedule.job-name:mongoToOracleJob}") String jobName) {
        this.jobLauncher = jobLauncher;
        this.ctx = ctx;
        this.jobName = jobName;
    }

    public static void main(String[] args) {
        SpringApplication.run(MigrationApplication.class, args);
    }

    @Scheduled(cron = "${migration.schedule.cron:0 0 2 * * *}")
    public void runScheduled() throws Exception {
        log.info("Scheduled trigger — launching job: {}", jobName);

        Job job = ctx.getBean(jobName, Job.class);

        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(job, params);
        log.info("Job '{}' finished with status: {}", jobName, execution.getStatus());
    }
}
