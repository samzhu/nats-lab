package com.example.consumer;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.SubscribableChannel;

/**
 * TimedChannel provides a binding/wiring point for the TimedSource. This is an effect of how
 * spring wants to bind.
 */
public interface TimedChannel {
    String INPUT = "timedchannelin";

    @Output(TimedChannel.INPUT)
    SubscribableChannel input();
}