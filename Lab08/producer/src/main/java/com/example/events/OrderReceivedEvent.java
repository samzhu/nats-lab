package com.example.events;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderReceivedEvent {
    private String eventID;
    private String orderID;
    private LocalDateTime time;
}
