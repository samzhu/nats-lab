package com.example.producer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.Message;
import io.nats.client.api.PublishAck;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduling {
    private final AtomicLong counter = new AtomicLong(0);
    private final String SUBJECT_PUBLISH = "ORDERS.received";
    private final Connection nc;
    private final AppCloudEventMessageUtils appCloudEventMessageUtils;

    @Scheduled(initialDelay = 1000, fixedRate = 2000)
    public void sendMessage() throws IOException, JetStreamApiException {

        JetStream jetStream = nc.jetStream();

        // StringBuffer sb = new StringBuffer();
        // sb.append(counter.incrementAndGet());

        // Headers headers = new Headers();
        // headers.add("id", "1234567890");

        // Message message = NatsMessage.builder()
        // .subject(SUBJECT_PUBLISH)
        // .data(sb.toString(), StandardCharsets.UTF_8)
        // .headers(headers)
        // .build();

        Message message = appCloudEventMessageUtils.convert(SUBJECT_PUBLISH,
                new OrderCreatedEvent(counter.incrementAndGet() + ""));

        CompletableFuture<PublishAck> future = jetStream.publishAsync(message);

        future.thenAccept((publishAck) -> {
            if (publishAck.hasError()) {
                log.error("發送消息失敗");
            } else {
                log.info("發送訂單編號");
            }
        });

        // PublishAck publishAck = jetStream.publish(SUBJECT_PUBLISH,
        // sb.toString().getBytes(StandardCharsets.UTF_8));
        // if (publishAck.hasError()) {
        // log.error("發送消息失敗");
        // } else {
        // log.info("發送訂單編號 {}", sb);
        // }

    }
}
