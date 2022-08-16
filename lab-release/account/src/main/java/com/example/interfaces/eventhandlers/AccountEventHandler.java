package com.example.interfaces.eventhandlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Iterator;
import java.util.Random;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.domain.events.AccountCreatedEvent;
import com.example.domain.events.MoneyDepositedEvent;
import com.example.domain.events.MoneyWithdrewEvent;
import com.example.infrastructure.brokers.NatsStream;
import com.example.interfaces.websocket.WebSocketService;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.JetStream;
import io.nats.client.JetStreamSubscription;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import io.nats.client.PushSubscribeOptions;
import io.nats.client.api.AckPolicy;
import io.nats.client.api.ConsumerConfiguration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class AccountEventHandler {
    MongoTemplate mongoTemplate;
    ObjectMapper objectMapper;
    Connection nc;
    WebSocketService webSocketService;

    String STREAM = "account";
    String SUBJECT = "account.*";
    String CONSUMER = "account";

    @Async
    public void receiveMessage(NatsStream natsStream) throws Exception {
        this.receiveMessagePush();
    }

    private void receiveMessagePush() throws Exception {
        JetStream jetStream = nc.jetStream();

        MessageHandler handler = (Message msg) -> {
            try {
                handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        ConsumerConfiguration configuration = ConsumerConfiguration.builder()
                .ackPolicy(AckPolicy.None) // don't want to worry about acking messages.
                .filterSubject(SUBJECT)
                .build();
        PushSubscribeOptions pushOptions = PushSubscribeOptions.builder()
                .durable(CONSUMER + "-" + new Random().nextInt(10))
                // .deliverGroup(CONSUMER + "-")
                .stream(STREAM)
                .configuration(configuration)
                .build();
        Dispatcher dispatcher = nc.createDispatcher();
        JetStreamSubscription sub = jetStream.subscribe(SUBJECT, dispatcher, handler, Boolean.FALSE, pushOptions);
        nc.flush(Duration.ofSeconds(1));
        log.info("開始接收訊息");
    }

    public void receiveMessagePull(NatsStream natsStream) throws Exception {
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

    private void handleMessage(Message message)
            throws ClassNotFoundException, StreamReadException, DatabindException, IOException {
        String className = message.getHeaders().get("ce-type").get(0);
        Class namedClass = Class.forName(className);
        Object event = objectMapper.readValue(message.getData(), namedClass);
        event = mongoTemplate.save(event, "account");
        log.info("event saved={}", event);
        if (message.isJetStream()) {
            // do something with the message
            // don't forget to ack based on your consumer AckPolicy configuration
            // or async auto ack setting
            message.ack();
            this.broadcastToFrontend(event);
        } else if (message.isStatusMessage()) {
            // status messages include heartbeat and flow control depending on
            // your consumer configuration
            System.out.println("Status " + message.getStatus());
        }
    }

    private void broadcastToFrontend(Object eventObj) {
        if (eventObj instanceof AccountCreatedEvent) {
            AccountCreatedEvent event = ((AccountCreatedEvent) eventObj);
            webSocketService.broadcast(String.format("帳號 %s 已建立", event.getAccountID()));
        }else if (eventObj instanceof MoneyDepositedEvent) {
            MoneyDepositedEvent event = ((MoneyDepositedEvent) eventObj);
            webSocketService.broadcast(String.format("帳號 %s 已加值 %d", event.getAccountID(), event.getAmountOfDeposited()));
        }else if (eventObj instanceof MoneyWithdrewEvent) {
            MoneyWithdrewEvent event = ((MoneyWithdrewEvent) eventObj);
            webSocketService.broadcast(String.format("帳號 %s 已扣除 %d", event.getAccountID(), event.getAmountOfWithdrew()));
        }
    }

}
