package com.example.interfaces.eventhandlers;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Iterator;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.infrastructure.brokers.NatsStream;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nats.client.JetStreamSubscription;
import io.nats.client.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventHandler {
    // private final NatsStream natsStream;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    private static String STREAM = "account";
    private static String SUBJECT = "account.*";

    @Async
    public void receiveMessage(NatsStream natsStream) throws Exception {
        JetStreamSubscription subscription = natsStream.createJetStreamAnonymousSubscription(STREAM, SUBJECT);
        log.info("開始接收訊息");
        while (true) {
            Iterator<Message> messages = subscription.iterate(1, Duration.ofMillis(1));
            while (messages.hasNext()) {
                Message message = messages.next();
                if (message.getHeaders() != null) {
                    message.getHeaders().forEach((k, v) -> log.info("k={}, v={}", k, v));
                }
                log.info("接收={}", new String(message.getData(), StandardCharsets.UTF_8));
                String className = message.getHeaders().get("ce-type").get(0);
                Class namedClass = Class.forName(className);
                log.info("load namedClass={}", namedClass);
                Object orderReceivedEvent = objectMapper.readValue(message.getData(), namedClass);
                log.info("Class={}", orderReceivedEvent);
                orderReceivedEvent = mongoTemplate.save(orderReceivedEvent, "account");
                log.info("save class={}", orderReceivedEvent);
                if (message.isJetStream()) {
                    // do something with the message
                    // don't forget to ack based on your consumer AckPolicy configuration
                    // or async auto ack setting
                    message.ack();
                } else if (message.isStatusMessage()) {
                    // status messages include heartbeat and flow control depending on
                    // your consumer configuration
                    System.out.println("Status " + message.getStatus());
                }
            }
        }
    }

}
