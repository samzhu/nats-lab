package com.example.events;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderReceivedEvent {
    @Id
    private String eventID;
    private String orderID;
    private LocalDateTime time;
}
