package com.example.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Application entry point.
 *
 * Usage:
 *   java -jar migration-app.jar                   → runs "mongoToOracleJob" (default)
 *   java -jar migration-app.jar mongoToOracleJob   → same, explicit
 *   java -jar migration-app.jar anotherMigrationJob → runs a different registered job
 *
 * Any Spring-managed Job bean is selectable by its bean name via the first CLI argument.
 */
@SpringBootApplication
public class MigrationApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MigrationApplication.class);

    private static final String DEFAULT_JOB = "mongoToOracleJob";

    private final JobLauncher jobLauncher;
    private final ApplicationContext ctx;

    public MigrationApplication(JobLauncher jobLauncher, ApplicationContext ctx) {
        this.jobLauncher = jobLauncher;
        this.ctx = ctx;
    }

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(MigrationApplication.class, args)));
    }

    @Override
    public void run(String... args) throws Exception {
        String jobName = (args.length > 0 && !args[0].isBlank()) ? args[0] : DEFAULT_JOB;
        log.info("Launching job: {}", jobName);

        Job job = ctx.getBean(jobName, Job.class);

        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(job, params);
        log.info("Job '{}' completed with status: {}", jobName, execution.getStatus());
    }
}
