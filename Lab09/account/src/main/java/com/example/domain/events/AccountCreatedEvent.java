package com.example.domain.events;

import com.example.domain.valueobjects.BalanceVO;

import lombok.Data;

@Data
public class AccountCreatedEvent {
    private String accountID;
    private BalanceVO balanceVO;
}
