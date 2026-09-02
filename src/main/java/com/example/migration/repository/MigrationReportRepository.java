package com.example.migration.repository;

import com.example.migration.domain.oracle.MigrationReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MigrationReportRepository extends JpaRepository<MigrationReportEntity, Long> {
}
