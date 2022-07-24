package com.example.consumer;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ConsumerConfiguration {


    @Bean
	public Consumer<Message<String>> sink() {
		return message -> {
			// log.info("message.getHeaders()={}", message.getHeaders());
			// log.info("message.getHeaders(ce-id)={}", message.getHeaders().get("ce-id"));
			// log.info("message.getPayload().getId()={}", message.getPayload().getId());
			log.info("message={}", message);
			// log.info("payload={}", message.getPayload());
		};
	}
    
}
