package com.example.migration.batch.processor;

import com.example.migration.domain.mongo.OrderDocument;
import com.example.migration.domain.oracle.OrderEntity;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Maps a MongoDB OrderDocument to an Oracle OrderEntity.
 *
 * Return null from process() to silently skip a record (Spring Batch will
 * count it as a skip and not pass it to the writer).
 */
@Component
public class OrderProcessor implements ItemProcessor<OrderDocument, OrderEntity> {

    @Override
    public OrderEntity process(OrderDocument doc) {
        if (doc == null) {
            return null;
        }

        OrderEntity entity = new OrderEntity();
        entity.setLegacyId(doc.getId());
        entity.setCustomer(doc.getCustomer());
        entity.setAmount(doc.getAmount());
        entity.setStatus(doc.getStatus());
        return entity;
    }
}
