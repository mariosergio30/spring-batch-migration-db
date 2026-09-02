package com.example.migration.listener;

import com.example.migration.domain.oracle.MigrationReportEntity;
import com.example.migration.repository.MigrationReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class Job1ExecutionListenerTest {

    @Mock private MigrationReportRepository reportRepo;

    @Test
    void writesSuccessSummaryRow() {
        Job1ExecutionListener listener = new Job1ExecutionListener(reportRepo);

        JobExecution jobExecution = buildJobExecution(BatchStatus.COMPLETED, 42L);
        listener.afterJob(jobExecution);

        ArgumentCaptor<MigrationReportEntity> captor = ArgumentCaptor.forClass(MigrationReportEntity.class);
        verify(reportRepo).save(captor.capture());

        MigrationReportEntity row = captor.getValue();
        assertThat(row.getType()).isEqualTo("JOB_SUMMARY");
        assertThat(row.getResult()).isEqualTo("SUCCESS");
        assertThat(row.getRecords()).isEqualTo(42);
        assertThat(row.getDescription()).contains("Written=42");
    }

    @Test
    void writesErrorSummaryRowOnFailure() {
        Job1ExecutionListener listener = new Job1ExecutionListener(reportRepo);

        JobExecution jobExecution = buildJobExecution(BatchStatus.FAILED, 5L);
        listener.afterJob(jobExecution);

        ArgumentCaptor<MigrationReportEntity> captor = ArgumentCaptor.forClass(MigrationReportEntity.class);
        verify(reportRepo).save(captor.capture());

        assertThat(captor.getValue().getResult()).isEqualTo("ERROR");
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private JobExecution buildJobExecution(BatchStatus status, long writeCount) {
        JobInstance instance = new JobInstance(1L, "mongoToOracleJob");
        JobParameters params = new JobParameters();
        JobExecution execution = new JobExecution(instance, params);
        execution.setStatus(status);

        StepExecution step = new StepExecution("job1Step", execution);
        // write count is stored in a private field; use reflection-free approach via status
        for (long i = 0; i < writeCount; i++) {
            step.incrementWriteCount(1);
        }
        execution.addStepExecutions(Set.of(step));
        return execution;
    }
}
