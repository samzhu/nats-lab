package com.example.consumer;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableBinding(Sink.class)
public class Receiver {

    @StreamListener(target = Sink.INPUT)
    // @StreamListener(target = TimedChannel.INPUT)
    public void chatReceive(Message<String> chatMessage) {
        System.out.println("收到: " + chatMessage.getPayload());
    }
}
