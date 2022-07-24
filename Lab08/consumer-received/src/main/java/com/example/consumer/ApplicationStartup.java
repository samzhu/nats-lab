package com.example.consumer;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Iterator;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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
    private static final String SUBJECT = "ORDERS.received";
    private static final String CONSUMER = "NEW";

    private static final String SUBJECT_PUBLISH = "ORDERS.processed";

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
                log.info("接收新訂單={}", new String(message.getData(), StandardCharsets.UTF_8));
                StringBuffer sb = new StringBuffer();
                sb.append("已接收到" + new String(message.getData(), StandardCharsets.UTF_8) + " 訂單");
                PublishAck publishAck = jetStream.publish(SUBJECT_PUBLISH,
                        sb.toString().getBytes(StandardCharsets.UTF_8));
                if (publishAck.hasError()) {
                    log.error("發送消息失敗");
                } else {
                    message.ack();
                }

            }
        }

    }

}
