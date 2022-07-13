package com.example.producer;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.cloudevent.CloudEventMessageBuilder;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.builder.CloudEventBuilder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Configuration
public class ProducerConfiguration {

    private AtomicLong counter = new AtomicLong(0);

    @Autowired
    private StreamBridge streamBridge;

    @Bean
    public Supplier<Flux<CloudEvent>> stringSupplier() {
        return () -> Flux.fromStream(Stream.generate(new Supplier<CloudEvent>() {
            @Override
            public CloudEvent get() {
                try {
                    Thread.sleep(1000);
                    String id = UUID.randomUUID().toString();
                    String msg = "message" + counter.incrementAndGet();

                    CloudEvent cloudEvent = CloudEventBuilder.fromSpecVersion(SpecVersion.V1)
                            .withId(id)
                            .withType("ORDER")
                            .withSource(URI.create("https://spring.io/foos"))
                            .withData(msg.getBytes())
                            .build();
                    Message<String> message = CloudEventMessageBuilder.withData(msg)
                            // .setSpecVersion("1.0")
                            // .setType("ORDER")
                            // .setId(id)
                            // .setSource(URI.create("https://spring.io/foos"))
                            .build();

                    log.info("id = {}, cloudEvent = {}", id, message);


                    log.info("id = {}, cloudEvent = {}", id, message);
                    return cloudEvent;
                } catch (Exception e) {
                    log.error("", e);
                }
                return null;
            }
        })).subscribeOn(Schedulers.elastic()).share();
    }

    // @Scheduled(fixedRate = 2000)
    public void tick() {
        String id = UUID.randomUUID().toString();
        String msg = "message" + counter.incrementAndGet();
        // log.info("id = {}, msg = {}", id, msg);
        // Message<String> message = CloudEventMessageBuilder.withData(msg)
        // .setSpecVersion("1.0")
        // .setType("ORDER")
        // .setId(id)
        // .setSource(URI.create("https://spring.io/foos"))
        // .build();

        CloudEvent cloudEvent = CloudEventBuilder.fromSpecVersion(SpecVersion.V1)
                .withId(id)
                .withType("ORDER")
                .withSource(URI.create("https://spring.io/foos"))
                .withData(msg.getBytes())
                .build();
        // MimeTypeUtils.APPLICATION_JSON
        log.info("id = {}, cloudEvent = {}", id, cloudEvent);
        streamBridge.send("stringSupplier-out-0", cloudEvent);
    }

}
