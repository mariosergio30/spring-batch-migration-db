package com.example.migration.batch.writer;

import com.example.migration.domain.oracle.MigrationReportEntity;
import com.example.migration.domain.oracle.OrderEntity;
import com.example.migration.repository.MigrationReportRepository;
import com.example.migration.repository.OrderOracleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * Persists each OrderEntity to Oracle individually so that a single failing
 * record does not abort the whole chunk.  Every error is recorded as a
 * RECORD_ERROR row in MIGRATION_REPORT.
 */
@Component
@StepScope
public class OracleOrderWriter implements ItemWriter<OrderEntity> {

    private static final Logger log = LoggerFactory.getLogger(OracleOrderWriter.class);

    private final OrderOracleRepository orderRepo;
    private final MigrationReportRepository reportRepo;

    public OracleOrderWriter(OrderOracleRepository orderRepo,
                             MigrationReportRepository reportRepo) {
        this.orderRepo  = orderRepo;
        this.reportRepo = reportRepo;
    }

    @Override
    public void write(Chunk<? extends OrderEntity> chunk) {
        for (OrderEntity order : chunk.getItems()) {
            try {
                orderRepo.save(order);
                log.debug("Saved order legacyId={}", order.getLegacyId());
            } catch (Exception ex) {
                log.error("Failed to save order legacyId={}: {}", order.getLegacyId(), ex.getMessage());
                reportRepo.save(buildErrorRow(order, ex));
            }
        }
    }

    private MigrationReportEntity buildErrorRow(OrderEntity order, Exception ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Unknown error";

        MigrationReportEntity row = new MigrationReportEntity();
        row.setMongoLegacyId(order.getLegacyId());
        row.setOrderId(order.getLegacyId());
        row.setType("RECORD_ERROR");
        row.setResult("ERROR");
        row.setDescription(msg.substring(0, Math.min(msg.length(), 4000)));
        row.setRecords(1);
        return row;
    }
}
