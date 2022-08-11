package com.example.internal.outboundservices;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import com.example.domain.events.AccountCreatedEvent;
import com.example.infrastructure.brokers.NatsStream;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nats.client.JetStream;
import io.nats.client.Message;
import io.nats.client.api.PublishAck;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventOutBound {
    private static String DEFAULT_ATTR_PREFIX = "ce-";
    private static final String _ID = "id";
    private static final String _SOURCE = "source";
    private static final String _SPECVERSION = "specversion";
    private static final String _TYPE = "type";
    private static final String _DATACONTENTTYPE = "datacontenttype";
    private static final String _SUBJECT = "subject";
    private static final String _TIME = "time";

    private final NatsStream natsStream;
    private final ObjectMapper objectMapper;

    public void publish(AccountCreatedEvent accountCreatedEvent) throws IOException {
        JetStream jetStream = natsStream.getJetStream();

        Headers headers = new Headers();
        headers.add(DEFAULT_ATTR_PREFIX + _ID, accountCreatedEvent.getEventID());
        headers.add(DEFAULT_ATTR_PREFIX + _SOURCE, "//projects/accounts");
        headers.add(DEFAULT_ATTR_PREFIX + _SPECVERSION, "1.0");
        headers.add(DEFAULT_ATTR_PREFIX + _TYPE, accountCreatedEvent.getClass().getName());
        headers.add(DEFAULT_ATTR_PREFIX + _TIME, accountCreatedEvent.getTime().toString());
        headers.add(DEFAULT_ATTR_PREFIX + _DATACONTENTTYPE, "application/json");
        headers.add(DEFAULT_ATTR_PREFIX + _SUBJECT, "account.created");

        Message message = NatsMessage.builder()
                .subject("account.created")
                .headers(headers)
                .data(objectMapper.writeValueAsString(accountCreatedEvent), StandardCharsets.UTF_8)
                .build();

        CompletableFuture<PublishAck> future = jetStream.publishAsync(message);
        future.thenAccept((publishAck) -> {
            if (publishAck.hasError()) {
                log.error("發送事件失敗");
            } else {
                log.info("發送事件已接收 {}", accountCreatedEvent);
            }
        });
    }

}
