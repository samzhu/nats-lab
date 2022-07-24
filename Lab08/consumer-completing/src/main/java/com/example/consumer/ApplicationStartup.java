package com.example.consumer;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Iterator;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.events.OrderCompleted;
import com.example.events.OrderProcessedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamSubscription;
import io.nats.client.Message;
import io.nats.client.PullSubscribeOptions;
import io.nats.client.api.PublishAck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartup {

    private static final String STREAM = "ORDERS_STR";
    private static final String SUBJECT = "ORDERS.processed";
    private static final String CONSUMER = "consumer-completing";

    private static final String SUBJECT_PUBLISH = "ORDERS.completed";

    private final ObjectMapper objectMapper;

    private final Connection nc;

    @EventListener(ApplicationReadyEvent.class)
    public void afterStartup() throws Exception {
        JetStream jetStream = nc.jetStream();
        PullSubscribeOptions pullOptions = PullSubscribeOptions.builder().durable(CONSUMER).stream(STREAM).build();

        JetStreamSubscription sub = jetStream.subscribe(SUBJECT, pullOptions);
        log.info("開始接收訊息");
        while (true) {
            Iterator<Message> messages = sub.iterate(100, Duration.ofSeconds(5));
            while (messages.hasNext()) {
                Message message = messages.next();
                if (message.getHeaders() != null) {
                    message.getHeaders().forEach((k, v) -> log.info("k={}, v={}", k, v));
                }
                OrderProcessedEvent orderProcessedEvent = objectMapper.readValue(message.getData(),
                        OrderProcessedEvent.class);
                log.info("接收已處理訂單={}", orderProcessedEvent);

                OrderCompleted orderCompleted = new OrderCompleted(orderProcessedEvent.getOrderID());

                PublishAck publishAck = jetStream.publish(SUBJECT_PUBLISH,
                        objectMapper.writeValueAsBytes(orderCompleted));
                if (publishAck.hasError()) {
                    log.error("發送消息失敗");
                } else {
                    message.ack();
                    log.info("發送訂單已完成 {}", orderCompleted);
                }
            }
        }

    }

}
