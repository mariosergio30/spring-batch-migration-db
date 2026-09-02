package com.example.migration.batch.processor;

import com.example.migration.domain.mongo.OrderDocument;
import com.example.migration.domain.oracle.OrderEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderProcessorTest {

    private final OrderProcessor processor = new OrderProcessor();

    @Test
    void mapsAllFieldsCorrectly() throws Exception {
        OrderDocument doc = new OrderDocument();
        doc.setId("abc123");
        doc.setCustomer("ACME Corp");
        doc.setAmount(new BigDecimal("99.99"));
        doc.setStatus("PENDING");

        OrderEntity result = processor.process(doc);

        assertThat(result).isNotNull();
        assertThat(result.getLegacyId()).isEqualTo("abc123");
        assertThat(result.getCustomer()).isEqualTo("ACME Corp");
        assertThat(result.getAmount()).isEqualByComparingTo("99.99");
        assertThat(result.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void returnsNullForNullInput() throws Exception {
        assertThat(processor.process(null)).isNull();
    }
}
