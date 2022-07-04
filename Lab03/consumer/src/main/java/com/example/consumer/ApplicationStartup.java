package com.example.consumer;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import io.nats.client.Options;
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
		Options options = new Options.Builder()
				.server("nats://localhost:34222")
				.reconnectWait(Duration.ofSeconds(1))
				.build();

		Connection nc = Nats.connect(options);

		// Use a latch to wait for 10 messages to arrive
		CountDownLatch latch = new CountDownLatch(10);

		// Create a dispatcher and inline message handler
		Dispatcher d = nc.createDispatcher((msg) -> {
			String str = new String(msg.getData(), StandardCharsets.UTF_8);
			System.out.println(str);
			latch.countDown();
		});

		// Subscribe
		d.subscribe("subject", "queuegroup1");

		// Wait for a message to come in
		latch.await();

		// Close the connection
		nc.close();

	}
}