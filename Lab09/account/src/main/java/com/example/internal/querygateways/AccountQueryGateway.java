package com.example.internal.querygateways;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.example.domain.aggregates.AccountAggregate;
import com.example.domain.events.AccountCreatedEvent;
import com.example.domain.events.BasicEvent;
import com.example.internal.outboundservices.EventOutBound;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class AccountQueryGateway {

    MongoTemplate mongoTemplate;
    EventOutBound eventOutBound;

    public AccountAggregate findByAccountID(String accountID) throws IOException {
        Criteria criteria = Criteria.where("accountID").is(accountID);
        Query query = new Query(criteria).with(Sort.by("time").descending());
        List<BasicEvent> events = mongoTemplate.find(query, BasicEvent.class, "account");
        AccountAggregate accountAggregate = new AccountAggregate(eventOutBound);
        for (BasicEvent event : events) {
            if (event instanceof AccountCreatedEvent) {
                accountAggregate.on((AccountCreatedEvent) event);
            }
        }
        log.info("Account={}", accountAggregate);
        return accountAggregate;
    }
}
