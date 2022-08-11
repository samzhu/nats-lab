package com.example;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.example.infrastructure.brokers.NatsStream;
import com.example.interfaces.eventhandlers.AccountEventHandler;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartup {

    private final NatsStream natsStream;
    private final AccountEventHandler accountEventHandler;
    private final MongoTemplate mongoTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void afterStartup() throws Exception {

        log.info("afterStartup");

        natsStream.createStream("account", "account.*");

        accountEventHandler.receiveMessage(natsStream);

        MongoCollection<Document> mongoCollection = mongoTemplate.getCollection("account");
        FindIterable iterable = mongoCollection.find(eq("accountID", "729595"));

        iterable.forEach(xx -> System.out.println(xx));

        // mongoTemplate.find(query, entityClass, collectionName);

        log.info("afterStartup 完成");

    }
}
