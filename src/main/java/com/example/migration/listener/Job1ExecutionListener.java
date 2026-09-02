package com.example.migration.listener;

import com.example.migration.domain.oracle.MigrationReportEntity;
import com.example.migration.repository.MigrationReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;

import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

/**
 * Writes a single JOB_SUMMARY row to MIGRATION_REPORT when a job finishes.
 *
 * The row captures:
 *   - total records written (across all steps/threads)
 *   - total records skipped due to processing errors
 *   - final job status (SUCCESS or ERROR)
 */
@Component
public class Job1ExecutionListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(Job1ExecutionListener.class);

    private final MigrationReportRepository reportRepo;

    public Job1ExecutionListener(MigrationReportRepository reportRepo) {
        this.reportRepo = reportRepo;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long writeCount = jobExecution.getStepExecutions().stream()
                .mapToLong(StepExecution::getWriteCount)
                .sum();

        long skipCount = jobExecution.getStepExecutions().stream()
                .mapToLong(StepExecution::getProcessSkipCount)
                .sum();

        boolean success = jobExecution.getStatus() == BatchStatus.COMPLETED;

        String description = String.format(
                "Job '%s' finished with status %s. Written=%d, Skipped=%d",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus(),
                writeCount,
                skipCount);

        log.info(description);

        MigrationReportEntity summary = new MigrationReportEntity();
        summary.setType("JOB_SUMMARY");
        summary.setResult(success ? "SUCCESS" : "ERROR");
        summary.setDescription(description);
        summary.setRecords((int) writeCount);
        reportRepo.save(summary);
    }
}
