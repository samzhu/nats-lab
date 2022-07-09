package com.example.producer;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * TimedChannel provides a binding/wiring point for the TimedSource. This is an effect of how
 * spring wants to bind.
 */
public interface TimedChannel {
    String OUTPUT = "timedchannel";

    @Output(TimedChannel.OUTPUT)
    MessageChannel output();
}