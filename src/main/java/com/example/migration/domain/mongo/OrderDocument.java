package com.example.migration.domain.mongo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "orders")
@Data
@NoArgsConstructor
public class OrderDocument {

    @Id
    private String id;          // MongoDB ObjectId stored as String → becomes legacyId in Oracle

    private String customer;

    private BigDecimal amount;

    private String status;

    private LocalDateTime createdAt;
}
