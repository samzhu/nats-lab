package com.example.consumer;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartup {

	@EventListener(ApplicationReadyEvent.class)
	public void afterStartup() throws Exception {
		this.receiveMessage();
	}

	// https://github.com/nats-io/nats.java
	private void receiveMessage() throws Exception {
		Connection nc = Nats.connect("nats://localhost:4222");
		Dispatcher d = nc.createDispatcher((msg) -> {
			String response = new String(msg.getData(), StandardCharsets.UTF_8);
			log.info("Received {}", response);
		});
		d.subscribe("subject");
	}
}