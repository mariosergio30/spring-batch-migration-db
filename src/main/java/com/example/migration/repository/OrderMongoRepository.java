package com.example.migration.repository;

import com.example.migration.domain.mongo.OrderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMongoRepository extends MongoRepository<OrderDocument, String> {
    // Spring Data provides findAll(), count(), etc. out of the box.
    // Add query methods here if job-specific filtering is needed.
}
