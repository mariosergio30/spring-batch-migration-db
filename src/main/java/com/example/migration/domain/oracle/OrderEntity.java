package com.example.migration.domain.oracle;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "ORDERS")
@Data
@NoArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_PK")
    private Long orderPk;

    @Column(name = "LEGACY_ID", nullable = false, length = 50)
    private String legacyId;

    @Column(name = "CUSTOMER", length = 200)
    private String customer;

    @Column(name = "AMOUNT", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "STATUS", length = 30)
    private String status;
}
