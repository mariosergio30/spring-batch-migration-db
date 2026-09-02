package com.example.migration.batch.reader;

import com.example.migration.domain.mongo.OrderDocument;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.data.MongoPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Reads OrderDocuments from MongoDB using cursor-based paging.
 *
 * setSaveState(false) is mandatory when a TaskExecutor is used because the
 * paging reader's internal page counter is not thread-safe.
 */
@Component
@StepScope
public class MongoOrderReader extends MongoPagingItemReader<OrderDocument> {

    public MongoOrderReader(
            MongoTemplate mongoTemplate,
            @Value("${migration.job1.page-size:500}") int pageSize) {

        setName("mongoOrderReader");
        setMongoTemplate(mongoTemplate);
        setTargetType(OrderDocument.class);
        setQuery(new Query());                          // reads all documents; add Criteria to filter
        setPageSize(pageSize);
        setSort(Map.of("_id", Sort.Direction.ASC));    // stable sort required for correct paging
        setSaveState(false);                            // thread-safe: no restart state persisted
    }
}
