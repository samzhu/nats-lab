package com.example.domain.events;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BasicEvent {
    String transactionID;
    @Id
    String eventID;
    String subject;
    LocalDateTime time;
}
