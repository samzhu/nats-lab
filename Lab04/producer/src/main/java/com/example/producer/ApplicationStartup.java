package com.example.producer;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.stream.IntStream;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartup {
	private static final String NATS_SERVER_URL = "nats://localhost:4222";
	private static final String SUBJECTS = "ORDERS.received";

	@EventListener(ApplicationReadyEvent.class)
	public void afterStartup() throws Exception {
		this.sendMessage();
	}

	// https://github.com/nats-io/nats.java
	private void sendMessage() throws Exception {
		Options options = new Options.Builder()
				.server(NATS_SERVER_URL)
				.reconnectWait(Duration.ofSeconds(1))
				.build();

		Connection nc = Nats.connect(options);
		IntStream.range(0, 20)
				.forEach(i -> nc.publish(SUBJECTS, ("hello world-" + i).getBytes(StandardCharsets.UTF_8)));
		nc.flush(Duration.ZERO);
		nc.close();
	}
}