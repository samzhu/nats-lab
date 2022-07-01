package com.example.producer;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.stream.IntStream;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.nats.client.Connection;
import io.nats.client.Nats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartup {

	@EventListener(ApplicationReadyEvent.class)
	public void afterStartup() throws Exception {
		this.sendMessage();
	}

	// https://github.com/nats-io/nats.java
	private void sendMessage() throws Exception {
		Connection nc = Nats.connect("nats://localhost:4222");
		IntStream.range(0, 10)
				.forEach(i -> nc.publish("subject", ("hello world-" + i).getBytes(StandardCharsets.UTF_8)));
		nc.flush(Duration.ZERO);
		nc.close();
	}
}