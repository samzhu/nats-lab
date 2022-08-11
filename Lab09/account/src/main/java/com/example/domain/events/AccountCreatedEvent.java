package com.example.domain.events;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.example.domain.valueobjects.BalanceVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreatedEvent {
    @Id
    private String eventID;
    private String accountID;
    private Integer balance;
    private LocalDateTime time;
}
