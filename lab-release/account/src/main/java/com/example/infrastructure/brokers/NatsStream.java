package com.example.infrastructure.brokers;

import java.io.IOException;
import java.time.Duration;

import org.springframework.stereotype.Component;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.JetStreamManagement;
import io.nats.client.JetStreamSubscription;
import io.nats.client.PullSubscribeOptions;
import io.nats.client.api.DiscardPolicy;
import io.nats.client.api.RetentionPolicy;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;
import io.nats.client.api.StreamInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NatsStream {

    private final Connection nc;

    public JetStream getJetStream() throws IOException {
        JetStream jetStream = nc.jetStream();
        return jetStream;
    }

    public PullSubscribeOptions createPullOptions(String stream, String consumer) {
        PullSubscribeOptions pullOptions = PullSubscribeOptions.builder().stream(stream).durable(consumer).build();
        return pullOptions;
    }

    public PullSubscribeOptions createAnonymousPullOptions(String stream) {
        // String consumer = NanoIdUtils.randomNanoId();
        String consumer = "account";
        PullSubscribeOptions pullOptions = PullSubscribeOptions.builder().stream(stream).durable(consumer).build();
        return pullOptions;
    }

    public JetStreamSubscription createJetStreamAnonymousSubscription(String stream, String subject) throws IOException, JetStreamApiException{
        PullSubscribeOptions pullOptions = this.createAnonymousPullOptions(stream);
        JetStreamSubscription subscription = this.getJetStream().subscribe(subject, pullOptions);
        return subscription;
    }

    public void createStream(String stream, String subject) throws IOException, JetStreamApiException {
        JetStreamManagement jsm = nc.jetStreamManagement();
        if (!jsm.getStreamNames().contains(stream)) {
            log.info("Stream {} 不存在", stream);
            StreamConfiguration streamConfiguration = StreamConfiguration.builder()
                    .name(stream)
                    .subjects(subject)
                    // .retentionPolicy(RetentionPolicy.WorkQueue)
                    .storageType(StorageType.Memory)
                    .replicas(1)
                    .discardPolicy(DiscardPolicy.Old)
                    .duplicateWindow(Duration.ofSeconds(30))
                    .build();
            StreamInfo streamInfo = nc.jetStreamManagement().addStream(streamConfiguration);
            log.info("Stream {} created.", stream);
            /*
             * name: 串流的名稱
             * subjects: 流中的主題
             * retentionPolicy: 消息的保留策略，默認為 LimitsPolicy
             * LimitsPolicy: 依據訊息數量 存儲空間 新舊消息
             * WorkQueuePolicy: 消息在被消費之前一直保留
             * InterestPolicy: 只要有消費者處於活躍狀態，消息就會被保存下來
             * ref https://docs.nats.io/using-nats/developer/develop_jetstream/model_deep_dive
             * storageType: 消息的存儲方式，有file和memory兩種
             * replicas: 消息在集群中的副本數量，只有集群才用得到，最大值為5
             * discardPolicy: 丟棄策略，默認為DiscardOld，即消息存儲到達上限時，將老的消息刪除
             * duplicateWindow: 對消息去重時使用的時間窗，建議盡可能小
             * ref: https://juejin.cn/post/6952792674655010853
             */
        } else {
            log.info("Stream {} 已存在", stream);
        }
    }

}
