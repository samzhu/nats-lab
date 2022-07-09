package com.example.producer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

@Component
@EnableScheduling
@EnableBinding(TimedChannel.class)
public class TimedSource {
    private static final Log logger = LogFactory.getLog(TimedSource.class);
    private AtomicLong counter = new AtomicLong(0);

    @Autowired
    private TimedChannel output;

    @Scheduled(fixedRate = 2000)
    public void tick() {
        String msg = "message " + counter.incrementAndGet();

        if (output == null) {
            logger.info("no output to send to - " + msg);
            return;
        }

        logger.info("sending - " + msg);
        output.output().send(MessageBuilder.withPayload(msg.getBytes(StandardCharsets.UTF_8)).build());
    }
}