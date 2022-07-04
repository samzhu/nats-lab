package com.example.consumer;

import java.time.Duration;
import java.time.ZonedDateTime;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamManagement;
import io.nats.client.Nats;
import io.nats.client.Options;
import io.nats.client.api.AckPolicy;
import io.nats.client.api.ConsumerConfiguration;
import io.nats.client.api.ConsumerInfo;
import io.nats.client.api.DeliverPolicy;
import io.nats.client.api.DiscardPolicy;
import io.nats.client.api.ReplayPolicy;
import io.nats.client.api.RetentionPolicy;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;
import io.nats.client.api.StreamInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartup {

	private static final String STREAM = "ORDER";
	private static final String SUBJECTS = "CONTRACT.CREATED";
	private static final String CONSUMER = "consumer-01";

	@EventListener(ApplicationReadyEvent.class)
	public void afterStartup() throws Exception {
		this.receiveMessage();
	}

	private void receiveMessage() throws Exception {
		Options options = new Options.Builder()
				.server("nats://localhost:34222")
				.reconnectWait(Duration.ofSeconds(1))
				.build();

		try (Connection nc = Nats.connect(options)) {
			JetStreamManagement jsm = nc.jetStreamManagement();
			// 如果 Stream 不存在則建立
			if (!jsm.getStreamNames().contains(STREAM)) {
				StreamConfiguration streamConfiguration = StreamConfiguration.builder()
						.name(STREAM)
						.subjects(SUBJECTS)
						.retentionPolicy(RetentionPolicy.WorkQueue)
						.storageType(StorageType.Memory)
						.replicas(1)
						.discardPolicy(DiscardPolicy.Old)
						.duplicateWindow(Duration.ofSeconds(30))
						.build();
				StreamInfo streamInfo = nc.jetStreamManagement().addStream(streamConfiguration);
				log.info("Stream {} created.", streamInfo);
				/*
				 * name: 串流的名稱
				 * subjects: 流中的主題
				 * retentionPolicy: 消息的保留策略，默認為 LimitsPolicy
				 *   LimitsPolicy: 依據訊息數量 存儲空間 新舊消息
				 *   WorkQueuePolicy: 消息在被消費之前一直保留
				 *   InterestPolicy: 只要有消費者處於活躍狀態，消息就會被保存下來
				 * ref:
				 * https://docs.nats.io/using-nats/developer/develop_jetstream/model_deep_dive
				 * storageType: 消息的存儲方式，有file和memory兩種
				 * replicas: 消息在集群中的副本數量，只有集群才用得到，最大值為5
				 * discardPolicy: 丟棄策略，默認為DiscardOld，即消息存儲到達上限時，將老的消息刪除
				 * duplicateWindow: 對消息去重時使用的時間窗，建議盡可能小
				 * ref: https://juejin.cn/post/6952792674655010853
				 */
			}

			// JetStream js = nc.jetStream();
			// Configure and create consumer
			ConsumerConfiguration configuration = ConsumerConfiguration.builder()
					.durable(CONSUMER)
					.filterSubject(SUBJECTS)
					.deliverPolicy(DeliverPolicy.ByStartTime)
					.startTime(ZonedDateTime.now())
					.replayPolicy(ReplayPolicy.Instant)
					// This requires every message to be specifically acknowledged, it's the only
					// supported option for pull-based Consumers
					.ackPolicy(AckPolicy.Explicit)
					.ackWait(Duration.ofSeconds(30))
					
					.maxDeliver(20)
					// .rateLimit(100)// consumer in pull mode can not have rate limit set [10086]
					.maxAckPending(20000)
					.build();
				/*
				 * durable: Consumer Name
				 * filterSubject: 選擇的主題，支持通配符
				 * deliverPolicy: 消費者的起始位置策略
				 *   All:
				 *   Last:
				 *   New:
				 *   ByStartSequence:
				 *   ByStartTime:
				 *   LastPerSubject:
				 * replayPolicy: 重放消息的發送方式
				 *   ReplayInstant: 盡可能快速收到
				 *   ReplayOriginal: 按照原本速率
				 *   
				 */

			ConsumerInfo consumerInfo = nc.jetStreamManagement()
					.addOrUpdateConsumer(STREAM, configuration);

		}

		// // Use a latch to wait for 10 messages to arrive
		// CountDownLatch latch = new CountDownLatch(10);

		// // Create a dispatcher and inline message handler
		// Dispatcher d = nc.createDispatcher((msg) -> {
		// String str = new String(msg.getData(), StandardCharsets.UTF_8);
		// System.out.println(str);
		// latch.countDown();
		// });

		// // Subscribe
		// d.subscribe("subject", "queuegroup1");

		// // Wait for a message to come in
		// latch.await();

		// // Close the connection
		// nc.close();

	}
}