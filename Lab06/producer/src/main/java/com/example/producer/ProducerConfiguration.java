package com.example.producer;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Configuration
public class ProducerConfiguration {

    private AtomicLong counter = new AtomicLong(0);

    @Autowired
	private StreamBridge streamBridge;

    // @Bean
    public Supplier<Flux<String>> stringSupplier() {
        return () -> Flux.fromStream(Stream.generate(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    Thread.sleep(1000);
                    String msg = "message" + counter.incrementAndGet();
                    log.info("msg = {}", msg);
                    return msg;
                } catch (Exception e) {
                    // ignore
                }
                return "Hello from Supplier";
            }
        })).subscribeOn(Schedulers.elastic()).share();
    }


    @Scheduled(fixedRate = 2000)
    public void tick() {
        String msg = "message" + counter.incrementAndGet();
        log.info("msg = {}", msg);
        streamBridge.send("stringSupplier-out-0", msg);
    }

}
