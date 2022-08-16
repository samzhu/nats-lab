package com.example;

import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.example.domain.events.BasicEvent;
import com.example.infrastructure.brokers.NatsStream;
import com.example.interfaces.eventhandlers.AccountEventHandler;
import com.example.internal.querygateways.AccountQueryGateway;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartup {

    private final NatsStream natsStream;
    private final AccountEventHandler accountEventHandler;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;
    private final AccountQueryGateway accountQueryGateway;

    @EventListener(ApplicationReadyEvent.class)
    public void afterStartup() throws Exception {

        log.info("afterStartup");

        natsStream.createStream("account", "account.*");

        accountEventHandler.receiveMessage(natsStream);

        accountQueryGateway.findByAccountID("701174");

        // MongoCollection<Document> mongoCollection = mongoTemplate.getCollection("account");
        // FindIterable<org.bson.Document> iterable = mongoCollection.find(eq("accountID", "727222"));

        // for (Document document : iterable) {
        //     System.out.println(document.getString("_class"));
        //     Class namedClass = Class.forName(document.getString("_class"));
        //     Object event = objectMapper.readValue(document.toJson(), namedClass);
        //     System.out.println(event);
        // }
        // Criteria criteria = Criteria.where("accountID").is("701174");
        // Query query = new Query(criteria);
        // List<BasicEvent> events = mongoTemplate.find(query, BasicEvent.class, "account");
        // log.info("events={}", events);

        log.info("afterStartup 完成");
    }
}
