package com.example.consumer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Random;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartup {

    private static final String STREAM = "ORDERS_STR";
    private static final String SUBJECT = "monitor.ORDERS";
    private static final String CONSUMER = "consumer-monitor" + new Random().nextInt(10);

    private final Connection nc;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;
    private final DataCenterProperties dataCenterProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void afterStartup() throws Exception {

        log.info("id={}", dataCenterProperties.getId());

        JetStream jetStream = nc.jetStream();

        MessageHandler handler = (Message msg) -> {
            // see handleMessage in above example
            try {
                handleMessage(msg);
            } catch (StreamReadException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (DatabindException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        };

        ConsumerConfiguration configuration = ConsumerConfiguration.builder()
                .ackPolicy(AckPolicy.None) // don't want to worry about acking messages.
                .filterSubject("ORDERS.*")
                .build();

        PushSubscribeOptions pushOptions = PushSubscribeOptions.builder()
                .durable(CONSUMER)
                .deliverGroup(CONSUMER + "-" + dataCenterProperties.getId())
                .stream(STREAM)
                .configuration(configuration)
                .build();
        log.info("CONSUMER={}, deliverGroup={}", CONSUMER, CONSUMER + "-" + dataCenterProperties.getId());
        Dispatcher dispatcher = nc.createDispatcher();

        JetStreamSubscription sub = jetStream.subscribe(SUBJECT, dispatcher, handler, Boolean.FALSE,
                pushOptions);
        nc.flush(Duration.ofSeconds(1));
        log.info("開始接收訊息");
        // handleMessage(sub.nextMessage(Duration.ofSeconds(1)));
    }

    void handleMessage(Message message)
            throws ClassNotFoundException, StreamReadException, DatabindException, IOException {
        if (message == null) {
            // the server had no message for us.
            // Maybe sleep here or do some housekeeping
        } else {
            log.info("已接收={}", new String(message.getData(), StandardCharsets.UTF_8));

            // if (message.getHeaders() != null) {
            // message.getHeaders().forEach((k, v) -> log.info("k={}, v={}", k, v));
            // }
            // String eventId = message.getHeaders().get(" ce-id").get(0);
            String className = message.getHeaders().get("ce-type").get(0);

            log.info("取得 ce-type={}", className);
            Class namedClass = Class.forName(className);
            log.info("load namedClass={}", namedClass);
            Object orderReceivedEvent = objectMapper.readValue(message.getData(), namedClass);
            log.info("Class={}", orderReceivedEvent);
            orderReceivedEvent = mongoTemplate.save(orderReceivedEvent, "orders");
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
