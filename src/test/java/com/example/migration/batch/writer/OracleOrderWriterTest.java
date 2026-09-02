package com.example.migration.batch.writer;

import com.example.migration.domain.oracle.MigrationReportEntity;
import com.example.migration.domain.oracle.OrderEntity;
import com.example.migration.repository.MigrationReportRepository;
import com.example.migration.repository.OrderOracleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OracleOrderWriterTest {

    @Mock private OrderOracleRepository orderRepo;
    @Mock private MigrationReportRepository reportRepo;

    private OracleOrderWriter writer;

    @BeforeEach
    void setUp() {
        writer = new OracleOrderWriter(orderRepo, reportRepo);
    }

    @Test
    void savesOrderSuccessfully() {
        OrderEntity order = order("id-1");
        writer.write(new Chunk<>(java.util.List.of(order)));

        verify(orderRepo).save(order);
        verify(reportRepo, never()).save(any());
    }

    @Test
    void writesErrorRowWhenSaveFails() {
        OrderEntity order = order("id-2");
        doThrow(new RuntimeException("Constraint violation")).when(orderRepo).save(order);

        writer.write(new Chunk<>(java.util.List.of(order)));

        ArgumentCaptor<MigrationReportEntity> captor = ArgumentCaptor.forClass(MigrationReportEntity.class);
        verify(reportRepo).save(captor.capture());

        MigrationReportEntity row = captor.getValue();
        assertThat(row.getMongoLegacyId()).isEqualTo("id-2");
        assertThat(row.getType()).isEqualTo("RECORD_ERROR");
        assertThat(row.getResult()).isEqualTo("ERROR");
        assertThat(row.getDescription()).contains("Constraint violation");
    }

    @Test
    void continuesAfterSingleFailure() {
        OrderEntity bad  = order("bad");
        OrderEntity good = order("good");
        doThrow(new RuntimeException("oops")).when(orderRepo).save(bad);

        writer.write(new Chunk<>(java.util.List.of(bad, good)));

        verify(orderRepo).save(bad);
        verify(orderRepo).save(good);
        verify(reportRepo, times(1)).save(any());
    }

    private OrderEntity order(String legacyId) {
        OrderEntity e = new OrderEntity();
        e.setLegacyId(legacyId);
        e.setCustomer("Customer");
        e.setAmount(BigDecimal.TEN);
        e.setStatus("NEW");
        return e;
    }
}
