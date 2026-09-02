package com.example.migration.domain.oracle;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Persistent record of migration outcomes.
 *
 * Rows are written in two situations:
 *   - RECORD_ERROR : a single document failed to insert (written by OracleOrderWriter).
 *   - JOB_SUMMARY  : one row per job execution summarising total written/skipped
 *                    (written by Job1ExecutionListener).
 *
 * created_at is intentionally omitted from the Java mapping so the Oracle DEFAULT
 * (SYSTIMESTAMP) is used instead of an application-side value.
 */
@Entity
@Table(name = "MIGRATION_REPORT")
@Data
@NoArgsConstructor
public class MigrationReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MIGRATION_REPORT_PK")
    private Long id;

    /** MongoDB document _id of the failed record (null for JOB_SUMMARY rows). */
    @Column(name = "MONGO_LEGACY_ID", length = 50)
    private String mongoLegacyId;

    /** Business-level order identifier (null for JOB_SUMMARY rows). */
    @Column(name = "ORDER_ID", length = 50)
    private String orderId;

    /** Row category: RECORD_ERROR | JOB_SUMMARY */
    @Column(name = "TYPE", length = 30)
    private String type;

    /** Outcome: OK | ERROR | WARNING | SUCCESS */
    @Column(name = "RESULT", length = 30)
    private String result;

    /** Human-readable message or exception detail (truncated to 4000 chars). */
    @Column(name = "DESCRIPTION", length = 4000)
    private String description;

    /** Number of records this row accounts for. */
    @Column(name = "RECORDS")
    private Integer records;
}
