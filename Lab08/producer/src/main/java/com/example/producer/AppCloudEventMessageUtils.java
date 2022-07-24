package com.example.producer;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Component;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.message.impl.MessageUtils;
import io.nats.client.Message;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppCloudEventMessageUtils {

    private final ObjectMapper objectMapper;

    private static final String _ID = "id";

    private static final String _SOURCE = "source";

    private static final String _SPECVERSION = "specversion";

    private static final String _TYPE = "type";

    private static final String _DATACONTENTTYPE = "datacontenttype";

    private static final String _DATASCHEMA = "dataschema";

    private static final String _SCHEMAURL = "schemaurl";

    private static final String _SUBJECT = "subject";

    private static final String _TIME = "time";

    /**
     * String value of 'cloudevent'. Typically used as
     * {@link MessageUtils#MESSAGE_TYPE}
     */
    public static String CLOUDEVENT_VALUE = "cloudevent";

    /**
     * String value of 'application/cloudevents' mime type.
     */
    public static String APPLICATION_CLOUDEVENTS_VALUE = "application/cloudevents";

    public static String DEFAULT_ATTR_PREFIX = "ce-";

    public Message convert(String subject, Object object) throws JsonProcessingException {
        Headers headers = new Headers();
        headers.add(DEFAULT_ATTR_PREFIX + _ID, NanoIdUtils.randomNanoId());
        headers.add(DEFAULT_ATTR_PREFIX + _SOURCE, "//projects/order");
        headers.add(DEFAULT_ATTR_PREFIX + _SPECVERSION, "1.0");
        headers.add(DEFAULT_ATTR_PREFIX + _TYPE, object.getClass().getName());
        headers.add(DEFAULT_ATTR_PREFIX + _TIME, OffsetDateTime.now().toString());
        headers.add(DEFAULT_ATTR_PREFIX + _DATACONTENTTYPE, "application/json");
        headers.add(DEFAULT_ATTR_PREFIX + _SUBJECT, subject);

        Message message = NatsMessage.builder()
                .subject(subject)
                .data(objectMapper.writeValueAsString(object), StandardCharsets.UTF_8)
                .headers(headers)
                .build();
        return message;
    }

}
