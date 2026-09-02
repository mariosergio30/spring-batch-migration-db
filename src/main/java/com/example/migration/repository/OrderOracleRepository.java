package com.example.migration.repository;

import com.example.migration.domain.oracle.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderOracleRepository extends JpaRepository<OrderEntity, Long> {
}
