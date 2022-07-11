package com.example.producer;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@EnableBinding(Source.class)
public class TimedSource {
    private static final Log logger = LogFactory.getLog(TimedSource.class);
    private AtomicLong counter = new AtomicLong(0);

    @Autowired
    private Source output;

    @Scheduled(fixedRate = 2000)
    public void tick() {
        String msg = "message " + counter.incrementAndGet();

        if (output == null) {
            logger.info("no output to send to - " + msg);
            return;
        }

        logger.info("sending - " + msg);
        Message< String > message = MessageBuilder.withPayload("Hi!! " + msg)
                    .build();
                    output.output().send(message);



        // output.output().send(MessageBuilder.withPayload(msg.getBytes(StandardCharsets.UTF_8)).build());
    }
}